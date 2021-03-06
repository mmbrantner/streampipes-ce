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

package org.streampipes.model.client.messages;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import java.util.List;

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS})
@RdfsClass(StreamPipes.ERROR_MESSAGE)
public class ErrorMessageLd extends MessageLd {

	public ErrorMessageLd() {
		super(false);
	}

	public ErrorMessageLd(NotificationLd...notifications) {
		super(false, notifications);
	}

	public ErrorMessageLd(List<NotificationLd> notifications) {
		super(false, notifications.toArray(new NotificationLd[0]));
	}

	public ErrorMessageLd(String elementName, List<NotificationLd> notifications) {
		super(false, notifications, elementName);
	}
}
