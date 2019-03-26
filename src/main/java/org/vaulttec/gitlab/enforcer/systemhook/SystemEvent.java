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
package org.vaulttec.gitlab.enforcer.systemhook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemEvent {
  @JsonProperty("created_at")
  private Date createdAt;
  @JsonProperty("event_name")
  private SystemEventName eventName;
  @JsonProperty("name")
  private String name;
  @JsonProperty("path")
  private String path;
  @JsonProperty("path_with_namespace")
  private String pathWithNamespace;
  @JsonAlias({ "project_id", "group_id" })
  private String id;

  public SystemEvent() {
    super();
  }

  public SystemEvent(SystemEventName eventName, String id, String name) {
    this.eventName = eventName;
    this.id = id;
    this.name = name;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public SystemEventName getEventName() {
    return eventName;
  }

  public void setEventName(SystemEventName eventName) {
    this.eventName = eventName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPathWithNamespace() {
    return pathWithNamespace;
  }

  public void setPathWithNamespace(String pathWithNamespace) {
    this.pathWithNamespace = pathWithNamespace;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "GLSystemEvent [" + eventName + " (" + path + ")]";
  }
}
