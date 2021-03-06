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

package org.streampipes.model.monitoring;

public class StreamPipesRuntimeError {

  private long timestamp;
  private String title;
  private String message;

  private String stackTrace;

  public StreamPipesRuntimeError(long timestamp, String title, String message, String stackTrace) {
    this.timestamp = timestamp;
    this.title = title;
    this.message = message;
    this.stackTrace = stackTrace;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public String getStackTrace() {
    return stackTrace;
  }
}
