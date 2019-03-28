/*
 * GitLab Enforcer
 * Copyright (c) 2019 Torsten Juergeleit
 * mailto:torsten AT vaulttec DOT org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaulttec.gitlab.enforcer.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessLevel {
  @JsonProperty("access_level")
  private Permission permission;
  @JsonProperty("user_id")
  private String userId;
  @JsonProperty("group_id")
  private String groupId;
  @JsonProperty("access_level_description")
  private String description;

  public AccessLevel() {
    super();
  }

  public AccessLevel(Permission permission) {
    this.permission = permission;
  }

  public Permission getPermission() {
    return permission;
  }

  public void setLevel(Permission permission) {
    this.permission = permission;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "GLAccessLevels [permission=" + permission + ", userId=" + userId + ", groupId=" + groupId + ", description="
        + description + "]";
  }
}
