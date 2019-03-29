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

public class SystemEventBuilder {
  private SystemEventName eventName;
  private String name;
  private String path;
  private String pathWithNamespace;
  private String id;

  public SystemEventBuilder eventName(SystemEventName eventName) {
    this.eventName = eventName;
    return this;
  }

  public SystemEventBuilder name(String name) {
    this.name = name;
    return this;
  }

  public SystemEventBuilder path(String path) {
    this.path = path;
    return this;
  }
  public SystemEventBuilder pathWithNamespace(String pathWithNamespace) {
    this.pathWithNamespace = pathWithNamespace;
    return this;
  }

  public SystemEventBuilder id(String id) {
    this.id = id;
    return this;
  }

  public SystemEvent build() {
    SystemEvent event = new SystemEvent();
    event.setCreatedAt(new Date(System.currentTimeMillis()));
    event.setEventName(eventName);
    event.setName(name);
    event.setPath(path);
    event.setPathWithNamespace(pathWithNamespace);
    event.setId(id);
    return event;
  }
}
