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

package org.streampipes.connect.rest.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.management.master.SourcesManagement;
import org.streampipes.connect.management.master.Utils;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.rest.shared.annotation.GsonWithIds;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.rest.shared.util.SpMediaType;
import org.streampipes.vocabulary.StreamPipes;

import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v1/{username}/master/sources")
public class SourcesResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(SourcesResource.class);

    private String connectContainerBaseUrl;

    private SourcesManagement sourcesManagement;

    public SourcesResource() {
        this.connectContainerBaseUrl = ConnectContainerConfig.INSTANCE.getConnectContainerMasterUrl();
        this.sourcesManagement = new SourcesManagement();
    }

    public SourcesResource(String connectContainerBaseUrl) {
        this.connectContainerBaseUrl = connectContainerBaseUrl;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Path("/")
    public Response getAllAdaptersInstallDescription(@PathParam("username") String username) {

        try {
            String resultingJson = this.sourcesManagement.getAllAdaptersInstallDescription(username);
            return ok(resultingJson);
        } catch (AdapterException e) {
            logger.error("Error while getting all adapter descriptions", e);
            return fail();
        }
    }



    @GET
    @Path("/{id}")
    @JsonLdSerialized
    @Produces(SpMediaType.JSONLD)
    public Response getAdapterDataSource(@PathParam("id") String id) {

        try {
            DataSourceDescription result = this.sourcesManagement.getAdapterDataSource(id);
            return ok(result);
        } catch (AdapterException e) {
            logger.error("Error while retrieving DataSourceDescription with id: " + id);
            return fail();
        }
    }



    @POST
//    @JsonLdSerialized
//    @Consumes(SpMediaType.JSONLD)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{streamId}/streams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(@PathParam("streamId") String elementId, String dataSetSet, @PathParam("username") String username) {

        SpDataSet dataSet = JsonLdUtils.fromJsonLd(dataSetSet, SpDataSet.class, StreamPipes.DATA_SET);

        String responseMessage = "Instance of data set " + dataSet.getUri() + " successfully started";

        String newUrl = Utils.addUserNameToApi(ConnectContainerConfig.INSTANCE.getConnectContainerWorkerUrl(), username);
        try {
            this.sourcesManagement.addAdapter(newUrl, elementId,  dataSet);
        } catch (AdapterException e) {
            logger.error("Could not set data set instance: " + dataSet.getUri(), e);
            return ok(Notifications.error("Could not set data set instance: " + dataSet.getUri()));
        }


        return ok(Notifications.success(responseMessage));
    }

    @DELETE
    @Path("/{streamId}/streams/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detach(@PathParam("streamId") String elementId, @PathParam("runningInstanceId") String runningInstanceId, @PathParam("username") String username) {
        String responseMessage = "Instance of set id: " + elementId  + " with instance id: "+ runningInstanceId + " successfully started";

        String newUrl = Utils.addUserNameToApi(ConnectContainerConfig.INSTANCE.getConnectContainerWorkerUrl(), username);

        try {
            this.sourcesManagement.detachAdapter(newUrl, elementId, runningInstanceId);
        } catch (AdapterException e) {
            logger.error("Could not set set id "+ elementId  + " with instance id: "+ runningInstanceId, e);
            return fail();
        }


        return ok(Notifications.success(responseMessage));
    }

    public void setSourcesManagement(SourcesManagement sourcesManagement) {
        this.sourcesManagement = sourcesManagement;
    }
}
