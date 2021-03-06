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
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.streampipes.connect.init.Config;
import org.streampipes.connect.management.master.DescriptionManagement;
import org.streampipes.connect.utils.ConnectContainerResourceTest;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.FormatDescriptionList;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.grounding.ProtocolDescriptionList;

import java.util.Arrays;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecriptionResourceTest extends ConnectContainerResourceTest {

    @Override
    protected String getApi() {
        return "/api/v1/riemer@fzi.de/master/description";
    }

    private Server server;

    private DescriptionResource descriptionResource;

    private DescriptionManagement descriptionManagement;


    @Before
    public  void before() {
        Config.MASTER_PORT = 8019;
        RestAssured.port = 8019;

        descriptionResource = new DescriptionResource();
        server = getMasterServer(descriptionResource);
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
    public void getFormatsEmptySuccess() {
        mockDescriptionManagerFormats(new FormatDescriptionList());

        FormatDescriptionList resultObject = getJsonLdSucessRequest("/formats", FormatDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#2");
        assertNotNull(resultObject.getList());
        assertEquals(resultObject.getList().size(), 0);
    }

    @Test
    public void getFormatsSuccess() {
        List<FormatDescription> list = Arrays.asList(
                new FormatDescription("http://id/1", "name1", ""),
                new FormatDescription("http://id/2", "name2", ""));
        mockDescriptionManagerFormats(new FormatDescriptionList(list));

        FormatDescriptionList resultObject = getJsonLdSucessRequest("/formats", FormatDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#2");
        assertNotNull(resultObject.getList());
        assertEquals(2, resultObject.getList().size());
        assertEquals("http://id/1", resultObject.getList().get(0).getUri());
        assertEquals("name1", resultObject.getList().get(0).getName());
        assertEquals("http://id/2", resultObject.getList().get(1).getUri());
        assertEquals("name2", resultObject.getList().get(1).getName());
    }

    @Test
    public void getProtocolsEmptySuccess() {
        mockDescriptionManagerProtocols(new ProtocolDescriptionList());

        ProtocolDescriptionList resultObject = getJsonLdSucessRequest("/protocols", ProtocolDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#1");
        assertNotNull(resultObject.getList());
        assertEquals(resultObject.getList().size(), 0);
    }

    @Test
    public void getProtocolsSuccess() {
        List<ProtocolDescription> list = Arrays.asList(
                new ProtocolDescription("http://id/1", "name1", ""),
                new ProtocolDescription("http://id/2", "name2", ""));
        mockDescriptionManagerProtocols(new ProtocolDescriptionList(list));

        ProtocolDescriptionList resultObject = getJsonLdSucessRequest("/protocols", ProtocolDescriptionList.class);

        assertEquals(resultObject.getUri(), "http://bla.de#1");
        assertNotNull(resultObject.getList());
        assertEquals(2, resultObject.getList().size());
        assertEquals("http://id/1", resultObject.getList().get(0).getUri());
        assertEquals("name1", resultObject.getList().get(0).getName());
        assertEquals("http://id/2", resultObject.getList().get(1).getUri());
        assertEquals("name2", resultObject.getList().get(1).getName());
    }

    @Test
    public void getAdaptersEmptySucess() {
        mockDescriptionManagerAdapters(new AdapterDescriptionList());

        AdapterDescriptionList resultObject = getJsonLdSucessRequest("/adapters", AdapterDescriptionList.class);

        assertNotNull(resultObject.getList());
        assertEquals(resultObject.getList().size(), 0);
    }

    // TODO
    // This test currently is not active. The problem is that we currently cannot deserialize the list with adapter
    // descriptions because AdpaterDesription is an abstract class and the concrete subclasses are not known.
    // Have a look at class org.streampipes.connect.management.AdapterDeserializer, which is a workaround for
    // AdapterDescriptions Objects
    //
//    @Test
//    public void getAdaptersSucess() {
//        List<AdapterDescription> list = Arrays.asList(
//                new GenericAdapterStreamDescription(),
//                new GenericAdapterSetDescription());
//        mockDescriptionManagerAdapters(new AdapterDescriptionList(list));
//
//        // TODO not sure how to fix
//        AdapterDescriptionList resultObject = getJsonLdSucessRequest("/adapters", AdapterDescriptionList.class, StreamPipes.ADAPTER_DESCRIPTION_LIST);
//
////        assertEquals(resultObject.getUri(), "http://bla.de#2");
//        assertNotNull(resultObject.getList());
//        assertEquals(2, resultObject.getList().size());
//        assertEquals("http://id/1", resultObject.getList().get(0).getUri());
//        assertEquals("name1", resultObject.getList().get(0).getName());
//        assertEquals("http://id/2", resultObject.getList().get(1).getUri());
//        assertEquals("name2", resultObject.getList().get(1).getName());
//    }

    private void mockDescriptionManagerFormats(FormatDescriptionList formatDescriptionList){
        DescriptionManagement descriptionManagement = mock(DescriptionManagement.class);
        when(descriptionManagement.getFormats()).thenReturn(formatDescriptionList);

        descriptionResource.setDescriptionManagement(descriptionManagement);
    }

    private void mockDescriptionManagerProtocols(ProtocolDescriptionList protocolDescriptionList){
        DescriptionManagement descriptionManagement = mock(DescriptionManagement.class);
        when(descriptionManagement.getProtocols()).thenReturn(protocolDescriptionList);

        descriptionResource.setDescriptionManagement(descriptionManagement);
    }

    private void mockDescriptionManagerAdapters(AdapterDescriptionList adapterDescriptionList){
        DescriptionManagement descriptionManagement = mock(DescriptionManagement.class);
        when(descriptionManagement.getAdapters()).thenReturn(adapterDescriptionList);

        descriptionResource.setDescriptionManagement(descriptionManagement);
    }
}