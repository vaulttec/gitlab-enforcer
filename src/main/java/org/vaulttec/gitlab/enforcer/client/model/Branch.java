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
public class Branch {
  private String name;
  @JsonProperty("protected")
  private boolean isProtected;
  @JsonProperty("developers_can_push")
  private boolean developersCanPush;
  @JsonProperty("developers_can_merge")
  private boolean developersCanMerge;
  @JsonProperty("can_push")
  private boolean canPush;

  public Branch() {
    this.isProtected = false;
    this.developersCanPush = true;
    this.developersCanMerge = false;
    this.canPush = true;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isProtected() {
    return isProtected;
  }

  public void setProtected(boolean isProtected) {
    this.isProtected = isProtected;
  }

  public boolean isDevelopersCanPush() {
    return developersCanPush;
  }

  public void setDevelopersCanPush(boolean developersCanPush) {
    this.developersCanPush = developersCanPush;
  }

  public boolean isDevelopersCanMerge() {
    return developersCanMerge;
  }

  public void setDevelopersCanMerge(boolean developersCanMerge) {
    this.developersCanMerge = developersCanMerge;
  }

  public boolean isCanPush() {
    return canPush;
  }

  public void setCanPush(boolean canPush) {
    this.canPush = canPush;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    Branch other = (Branch) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "GLBranch [name=" + name + ", isProtected=" + isProtected + ", developersCanPush=" + developersCanPush
        + ", developersCanMerge=" + developersCanMerge + ", canMerge=" + canPush + "]";
  }
}
