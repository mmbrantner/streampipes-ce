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
package org.streampipes.model.client.version;

public class VersionedItem {

  private ItemType itemType;
  private String itemName;
  private String itemVersion;

  public VersionedItem(ItemType itemType, String itemName, String itemVersion) {
    this.itemType = itemType;
    this.itemName = itemName;
    this.itemVersion = itemVersion;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public String getItemVersion() {
    return itemVersion;
  }

  public void setItemVersion(String itemVersion) {
    this.itemVersion = itemVersion;
  }
}
