/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic;

import org.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.model.schema.EventSchema;

public class Util {

    public static EventSchema getEventSchema(GenericAdapterDescription adapterDescription) {
        if(adapterDescription instanceof GenericAdapterStreamDescription) {
            return ((GenericAdapterStreamDescription) adapterDescription).getDataStream().getEventSchema();
        } else if (adapterDescription instanceof GenericAdapterSetDescription) {
            return ((GenericAdapterSetDescription) adapterDescription).getDataSet().getEventSchema();

        }
        return null;
    }
}
