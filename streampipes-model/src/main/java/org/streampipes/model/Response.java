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

package org.streampipes.model;

import com.google.gson.Gson;

public class Response {

	private Boolean success;
	
	private String elementId;
	
	private String optionalMessage;
	
	public Response(String elementId, boolean success)
	{
        this(elementId, success, "");
	}
	
	public Response(String elementId, boolean success, String optionalMessage)
	{
		this.elementId = elementId;
		this.success = success;
		this.optionalMessage = optionalMessage;
	}

	public Boolean isSuccess() {
		return success;
	}

	public String getElementId() {
		return elementId;
	}

	public String getOptionalMessage() {
		return optionalMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result + ((optionalMessage == null) ? 0 : optionalMessage.hashCode());
		result = prime * result + (success ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Response other = (Response) obj;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (optionalMessage == null) {
			if (other.optionalMessage != null)
				return false;
		} else if (!optionalMessage.equals(other.optionalMessage))
			return false;
		if (success != other.success)
			return false;
		return true;
	}


	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
