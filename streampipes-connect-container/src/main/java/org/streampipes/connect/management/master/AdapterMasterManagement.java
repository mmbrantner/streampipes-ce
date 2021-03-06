/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.management.master;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.GroundingService;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.adapter.AdapterSetDescription;
import org.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public class AdapterMasterManagement {

    private static final Logger logger = LoggerFactory.getLogger(AdapterMasterManagement.class);

    public static void startAllStreamAdapters() throws AdapterException {
        AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

        for (AdapterDescription ad : allAdapters) {
            if (ad instanceof AdapterStreamDescription) {
                String url = Utils.addUserNameToApi(ConnectContainerConfig.INSTANCE.getConnectContainerWorkerUrl(), ad.getUserName());

                WorkerRestClient.invokeStreamAdapter(url, (AdapterStreamDescription) ad);
            }
        }
    }

    public String addAdapter(AdapterDescription ad, String baseUrl, AdapterStorageImpl
            adapterStorage, String username)
            throws AdapterException {

        // Add EventGrounding to AdapterDescription
        EventGrounding eventGrounding = GroundingService.createEventGrounding(
                ConnectContainerConfig.INSTANCE.getKafkaHost(), ConnectContainerConfig.INSTANCE.getKafkaPort(), null);
        ad.setEventGrounding(eventGrounding);

        // Old dele
//        String newId = ad.getElementId() + UUID.randomUUID().toString();
        String uuid =  UUID.randomUUID().toString();

        String newId = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl() + "api/v1/" + username + "/master/sources/" + uuid;

        ad.setElementId(newId);

        // store in db
        adapterStorage.storeAdapter(ad);

        // start when stream adapter
        if (ad instanceof AdapterStreamDescription) {
            // TODO
            WorkerRestClient.invokeStreamAdapter(baseUrl, (AdapterStreamDescription) ad);
            System.out.println("Start adapter");
//            SpConnect.startStreamAdapter((AdapterStreamDescription) ad, baseUrl);
        }

        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();
        String adapterCouchdbId = "";
        for (AdapterDescription a : allAdapters) {
           if (a.getElementId().equals(ad.getElementId())) {
               adapterCouchdbId = a.getId();
           }
        }


        // backend url is used to install data source in streampipes
        String backendBaseUrl = "http://" + ConnectContainerConfig.INSTANCE.getBackendApiUrl() +"api/v2/";
        String requestUrl = backendBaseUrl +  "noauth/users/" + username + "/element";

        // the backend can install the data source via the element url
//        String elementUrl = newId;

//        String elementUrl = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl() +
//                "api/v1/" + username + "/master/sources/" + adapterCouchdbId;

        String elementUrl = newId;

        logger.info("Install source (source URL: " + elementUrl +" in backend over URL: " + requestUrl);

        installDataSource(requestUrl, elementUrl);

        return new SourcesManagement().getAdapterDataSource(elementUrl).getElementId();
    }

    public boolean installDataSource(String requestUrl, String elementIdUrl) throws AdapterException {

        try {
            String responseString = Request.Post(requestUrl)
                    .bodyForm(
                            Form.form()
                                    .add("uri", elementIdUrl)
                                    .add("publicElement", "true").build())
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();

            logger.info(responseString);
        } catch (IOException e) {
            logger.error("Error while installing data source: " + requestUrl, e);
            throw new AdapterException();
        }

        return true;
    }

    public AdapterDescription getAdapter(String id, AdapterStorageImpl adapterStorage) throws AdapterException {

        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

        if (allAdapters != null && id != null) {
            for (AdapterDescription ad : allAdapters) {
                if (id.equals(ad.getAdapterId())) {
                    return ad;
                }
            }
        }

        throw new AdapterException("Could not find adapter with id: " + id);
    }

    public void deleteAdapter(String id, String baseUrl) throws AdapterException {
        //        // IF Stream adapter delete it
        AdapterStorageImpl adapterStorage = new AdapterStorageImpl();
        boolean isStreamAdapter = isStreamAdapter(id, adapterStorage);

        if (isStreamAdapter) {
            stopStreamAdapter(id, baseUrl, adapterStorage);
        }
        AdapterDescription ad = adapterStorage.getAdapter(id);
        String username = ad.getUserName();

        adapterStorage.deleteAdapter(id);

        String backendBaseUrl = "http://" + ConnectContainerConfig.INSTANCE.getBackendApiUrl() + "api/v2/noauth/users/"+ username + "/element/delete";

//        String elementUrl = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl() + "api/v1/" + username + "/master/sources/" + id;
//        String elementUrl = ad.getUri() + "/" + ad.getId();
        String elementUrl = ad.getUri();

//        deleteDataSource(backendBaseUrl, elementUrl);


        boolean response = true;

        String responseString = null;
        logger.info("Delete data source in backend with request URL: " + backendBaseUrl);
        try {
            responseString = Request.Post(backendBaseUrl)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .bodyForm(Form.form()
                            .add("uri", elementUrl).build())
                    .execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
            responseString = e.toString();
            response = false;
        }

        logger.info("Response of the deletion request" + responseString);
    }

    public List<AdapterDescription> getAllAdapters(AdapterStorageImpl adapterStorage) throws AdapterException {

        List<AdapterDescription> allAdapters = adapterStorage.getAllAdapters();

        if (allAdapters == null) {
            throw new AdapterException("Could not get all adapters");
        }

        return allAdapters;
    }

    public static void stopSetAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) throws AdapterException {

        AdapterSetDescription ad = (AdapterSetDescription) adapterStorage.getAdapter(adapterId);

        WorkerRestClient.stopSetAdapter(baseUrl, ad);
    }

    public static void stopStreamAdapter(String adapterId, String baseUrl, AdapterStorageImpl adapterStorage) throws AdapterException {
        AdapterStreamDescription ad = (AdapterStreamDescription) adapterStorage.getAdapter(adapterId);

        WorkerRestClient.stopStreamAdapter(baseUrl, ad);
    }

    public static boolean isStreamAdapter(String id, AdapterStorageImpl adapterStorage) {
        AdapterDescription ad = adapterStorage.getAdapter(id);

        return ad instanceof AdapterStreamDescription;
    }

    private static <T> String toJsonLd(T object) {
        JsonLdUtils.toJsonLD(object);
        String s = JsonLdUtils.toJsonLD(object);

        if (s == null) {
            logger.error("Could not serialize Object " + object + " into json ld");
        }

        return s;
    }
}
