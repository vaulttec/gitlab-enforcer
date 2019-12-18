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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtectedBranch {
  private String name;
  @JsonProperty("push_access_levels")
  private List<AccessLevel> pushAccessLevels;
  @JsonProperty("merge_access_levels")
  private List<AccessLevel> mergeAccessLevels;
  @JsonProperty("unprotect_access_levels")
  private List<AccessLevel> unprotectAccessLevels;

  public ProtectedBranch() {
    super();
  }

  public ProtectedBranch(String name) {
    this.name = name;
    this.pushAccessLevels = new ArrayList<>();
    this.mergeAccessLevels = new ArrayList<>();
    this.unprotectAccessLevels = new ArrayList<>();
  }

  public ProtectedBranch(String name, String... settings) {
    this(name);
    for (int i = 0; i < settings.length; i += 2) {
      List<AccessLevel> levels = getAccessLevelsByName(settings[i]);
      if (levels != null) {
        levels.add(new AccessLevel(Permission.fromAccessLevel(settings[i + 1])));
      }
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<AccessLevel> getPushAccessLevels() {
    return pushAccessLevels;
  }

  public void setPushAccessLevels(List<AccessLevel> accessLevels) {
    this.pushAccessLevels = accessLevels;
  }

  public List<AccessLevel> getMergeAccessLevels() {
    return mergeAccessLevels;
  }

  public void setMergeAccessLevels(List<AccessLevel> accessLevels) {
    this.mergeAccessLevels = accessLevels;
  }

  public List<AccessLevel> getUnprotectAccessLevels() {
    return unprotectAccessLevels;
  }

  public void setUnprotectAccessLevels(List<AccessLevel> accessLevels) {
    this.unprotectAccessLevels = accessLevels;
  }

  public List<AccessLevel> getAccessLevelsByName(String name) {
    switch (name) {
    case "push_access_level":
      return pushAccessLevels;
    case "merge_access_level":
      return mergeAccessLevels;
    case "unprotect_access_level":
      return unprotectAccessLevels;
    }
    return null;
  }

  public boolean hasAccessLevel(String name, Permission permission) {
    List<AccessLevel> levels = getAccessLevelsByName(name);
    if (levels != null && !levels.isEmpty()) {
      for (AccessLevel level : levels) {
        if (level.getPermission().equals(permission)) {
          return true;
        }
      }
    }
    return false;
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ProtectedBranch other = (ProtectedBranch) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ProtectedBranch [name=" + name + ", pushAccessLevels=" + pushAccessLevels + ", mergeAccessLevels="
        + mergeAccessLevels + ", unprotectAccessLevels=" + unprotectAccessLevels + "]";
  }
}
