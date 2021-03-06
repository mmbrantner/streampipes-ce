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


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.master.SourcesManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.model.SpDataSet;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.rest.shared.util.JsonLdUtils;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SourcesResourceTest extends ConnectContainerResourceTest {

    @Override
    protected String getApi() {
        return "/api/v1/riemer@fzi.de/master/sources";
    }

    private Server server;

    private SourcesResource sourcesResource;

    private SourcesManagement sourcesManagement;


    @Before
    public  void before() {
        Config.MASTER_PORT = 8019;
        RestAssured.port = 8019;

        sourcesResource = new SourcesResource("");
        server = getMasterServer(sourcesResource);
    }

    @After
    public void after() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getAllAdaptersInstallDescriptionSuccess() throws Exception {
        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
        when(sourcesManagement.getAllAdaptersInstallDescription(anyString())).thenReturn("test");
        sourcesResource.setSourcesManagement(sourcesManagement);

        Response response = given().contentType("application/json")
                .when()
                .get(getApi() + "/");

        response.then()
                .statusCode(200);

        String resultString = response.body().print();


        assertEquals("test", resultString);

    }

    @Test
    public void getAllAdaptersInstallDescriptionFail() throws Exception {
        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
        doThrow(new AdapterException()).when(sourcesManagement).getAllAdaptersInstallDescription(anyString());
        sourcesResource.setSourcesManagement(sourcesManagement);

        given().contentType("application/json")
                .when()
                .get(getApi() + "/")
                .then()
                .statusCode(500);
    }

    @Test
    public void getAdapterDataSourceSuccess() throws AdapterException {
        DataSourceDescription dataSourceDescription = new DataSourceDescription("http://a.d", "a", "");
        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
        when(sourcesManagement.getAdapterDataSource(anyString())).thenReturn(dataSourceDescription);
        sourcesResource.setSourcesManagement(sourcesManagement);

        Response response = given().contentType("application/json")
                .when()
                .get(getApi() + "/1234");

        response.then()
                .statusCode(200);

        String resultString = response.body().print();

        DataSourceDescription result = JsonLdUtils.fromJsonLd(resultString, DataSourceDescription.class);

        assertEquals(dataSourceDescription.getUri(), result.getUri());
        assertEquals(dataSourceDescription.getName(), result.getName());
    }

    @Test
    public void getAdapterDataSourceFail() throws AdapterException {
        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
        doThrow(new AdapterException()).when(sourcesManagement).getAdapterDataSource(anyString());
        sourcesResource.setSourcesManagement(sourcesManagement);

        given().contentType("application/json")
                .when()
                .get(getApi() + "/1234")
                .then()
                .statusCode(500);
    }

//
//    @Test
//    public void addAdapterSuccess() throws Exception {
//        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
//        doNothing().when(sourcesManagement).addAdapter(anyString(), anyString(), any());
//        sourcesResource.setSourcesManagement(sourcesManagement);
//
//        String data = getMinimalDataSetJsonLd();
//        postJsonSuccessRequest(data, "/id/streams", "Instance of data set http://dataset.de/1 successfully started");
//
//        verify(sourcesManagement, times(1)).addAdapter(anyString(), anyString(), any());
//    }
//
//    @Test
//    public void addAdapterFail() throws AdapterException {
//        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
//        doThrow(AdapterException.class).when(sourcesManagement).addAdapter(anyString(), anyString(), any());
//        sourcesResource.setSourcesManagement(sourcesManagement);
//
//        String data = getMinimalDataSetJsonLd();
//        postJsonFailRequest(data, "/id/streams", "Could not set data set instance: http://dataset.de/1");
//
//    }

//    @Test
//    public void detachSuccess() throws AdapterException {
//        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
//        doNothing().when(sourcesManagement).detachAdapter(anyString(), anyString(), anyString());
//        sourcesResource.setSourcesManagement(sourcesManagement);
//
//        deleteJsonLdSucessRequest("/id0/streams/id1");
//
//        verify(sourcesManagement, times(1)).detachAdapter(anyString(), anyString(), anyString());
//    }

    @Test
    public void detachFail() throws AdapterException {
        SourcesManagement sourcesManagement = mock(SourcesManagement.class);
        doThrow(AdapterException.class).when(sourcesManagement).detachAdapter(anyString(), anyString(), anyString());
        sourcesResource.setSourcesManagement(sourcesManagement);

        deleteJsonLdFailRequest("/id0/streams/id1");
    }

    private String getMinimalDataSetJsonLd() {
        SpDataSet dataSet = new SpDataSet();
        dataSet.setUri("http://dataset.de/1");

        return JsonLdUtils.toJsonLD(dataSet);
    }
}