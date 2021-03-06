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

import java.util.List;

import org.vaulttec.gitlab.enforcer.client.model.Namespace.Kind;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
  private String id;
  private String path;
  @JsonProperty("path_with_namespace")
  private String pathWithNamespace;
  private String name;
  private Namespace namespace;
  @JsonProperty("shared_with_groups")
  private List<Group> sharedWithGroups;

  public Project() {
    super();
  }

  public Project(String id, String name, Namespace namespace, List<Group> sharedWithGroups) {
    this.id = id;
    this.name = name;
    this.namespace = namespace;
    this.sharedWithGroups = sharedWithGroups;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Namespace getNamespace() {
    return namespace;
  }

  public void setNamespace(Namespace namespace) {
    this.namespace = namespace;
  }

  public List<Group> getSharedWithGroups() {
    return sharedWithGroups;
  }

  public void setSharedWithGroups(List<Group> sharedWithGroups) {
    this.sharedWithGroups = sharedWithGroups;
  }

  public Kind getKind() {
    return namespace != null ? namespace.getKind() : null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Project other = (Project) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Project [id=" + id + ", path=" + path + ", name=" + name + ", sharedWithGroups=" + sharedWithGroups + "]";
  }
}
