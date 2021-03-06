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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.exception.ParseException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;

import java.util.Arrays;
import java.util.Optional;

public class GuessManagement {

    private static Logger logger = LoggerFactory.getLogger(GuessSchema.class);

    public GuessSchema guessSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException {

        Adapter adapter = AdapterRegistry.getAdapter(adapterDescription);

        GuessSchema guessSchema;
        try {
            guessSchema = adapter.getSchema(adapterDescription);
            for (int i = 0; i < guessSchema.getEventSchema().getEventProperties().size(); i++) {
                guessSchema.getEventSchema().getEventProperties().get(i).setIndex(i);
            }
        } catch (ParseException e) {
            logger.error(e.toString());

            String errorClass = "";
            Optional<StackTraceElement> stackTraceElement = Arrays.stream(e.getStackTrace()).findFirst();
            if(stackTraceElement.isPresent()) {
                String[] errorClassLong = stackTraceElement.get().getClassName().split("\\.");
                errorClass = errorClassLong[errorClassLong.length - 1] + ": ";
            }

            throw new ParseException(errorClass + e.getMessage());
        } catch (Exception e) {
            logger.error("Unknown Error: " + e.toString());
            throw new AdapterException(e.toString());
        }

        return guessSchema;

    }

    public void guessFormat() {
        // TODO implement
    }


    public void  guessFormatDescription() {
        // TODO implement
    }



}
