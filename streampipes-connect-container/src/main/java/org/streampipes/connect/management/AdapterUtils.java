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

package org.streampipes.connect.management;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.config.ConnectContainerConfig;

import java.io.IOException;

public class AdapterUtils {
    private static final Logger logger = LoggerFactory.getLogger(AdapterUtils .class);

    public static String stopPipeline(String url) {
        logger.info("Send stopAdapter pipeline request on URL: " + url);

        String result = "";
        try {
            result = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(100000)
                    .execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }

        logger.info("Successfully stopped pipeline");

        return result;
    }

    public static String getUrl(String baseUrl, String pipelineId) {
        return "http://" +baseUrl + "api/v2/pipelines/" + pipelineId + "/stopAdapter";
    }
}
