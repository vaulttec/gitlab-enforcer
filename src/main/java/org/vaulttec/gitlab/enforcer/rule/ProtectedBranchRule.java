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
package org.vaulttec.gitlab.enforcer.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.vaulttec.gitlab.enforcer.EnforcerEvents;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;
import org.vaulttec.gitlab.enforcer.client.model.AccessLevel;
import org.vaulttec.gitlab.enforcer.client.model.Namespace.Kind;
import org.vaulttec.gitlab.enforcer.client.model.Permission;
import org.vaulttec.gitlab.enforcer.client.model.ProtectedBranch;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

public class ProtectedBranchRule extends AbstractRule {

  private static final Logger LOG = LoggerFactory.getLogger(ProtectedBranchRule.class);

  private String name;
  private boolean skipUserProjects;
  private boolean keepStricterAccessLevel;
  private String[] settings;
  private String[] settingsInfo;

  @Override
  public String getInfo() {
    StringBuffer info = new StringBuffer("Enforce Protected Branch");
    if (name != null) {
      info.append(" (").append(String.join(", ", settingsInfo)).append(")");
    }
    return info.toString();
  }

  @Override
  public boolean supports(SystemEvent event) {
    return SystemEventName.PROJECT_CREATE.equals(event.getEventName());
  }

  @Override
  public void doInit(Map<String, String> config) {
    this.name = config.get("name");
    if (!StringUtils.hasText(this.name)) {
      throw new IllegalStateException("Missing branch name");
    }
    String skipUserProjectsText = config.get("skipUserProjects");
    if (StringUtils.hasText(skipUserProjectsText)) {
      this.skipUserProjects = Boolean.parseBoolean(skipUserProjectsText);
    }
    String keepStricterAccessLevelText = config.get("keepStricterAccessLevel");
    if (StringUtils.hasText(keepStricterAccessLevelText)) {
      this.keepStricterAccessLevel = Boolean.parseBoolean(keepStricterAccessLevelText);
    }
    this.settings = config.entrySet().stream()
        .filter(e -> !"name".equals(e.getKey()) && !"skipUserProjects".equals(e.getKey())
            && !"keepStricterAccessLevel".equals(e.getKey()))
        .flatMap(e -> Arrays.asList(e.getKey(), e.getValue()).stream()).toArray(size -> new String[size]);
    List<String> settingsList = new ArrayList<>();
    settingsList.add("skipUserProject=" + skipUserProjects);
    settingsList.add("keepStricterAccessLevel=" + keepStricterAccessLevel);
    settingsList.add("name=" + name);
    for (int i = 0; i < settings.length; i += 2) {
      settingsList.add(settings[i] + "=" + settings[i + 1]);
    }
    this.settingsInfo = settingsList.toArray(new String[settingsList.size()]);
  }

  @Override
  public void doHandle(EnforcerExecution execution, SystemEvent event) {
    if (!skipUserProjects || client.getProject(event.getId()).getKind() != Kind.USER) {
      // Create a mutable list of the initial protected branch configuration
      // This list will be modified in hasStricterSettings()!!!
      List<String> enforcedSettings = Arrays.stream(settings).collect(Collectors.toList());
      Optional<ProtectedBranch> existingBranch = Optional.empty();

      // First check if the protected branch already exists
      List<ProtectedBranch> branches = client.getProtectedBranchesForProject(event.getId());
      if (branches != null) {
        existingBranch = branches.stream().filter(branch -> name.equals(branch.getName())).findFirst();
        if (existingBranch.isPresent()) {
          // If the existing branch has already the required access levels then we are
          // set
          if (hasRequiredAccessLevels(existingBranch.get())) {
            return;
          }
          // If the existing branch has stricter access levels then we are set as well
          if (keepStricterAccessLevel && hasStricterSettings(existingBranch.get(), enforcedSettings)) {
            return;
          }
        }
      }
      LOG.info("Enforcing protected branch '{}' in project '{}'", name, event.getPathWithNamespace());

      // An existing branch with different settings has to be removed - otherwise we
      // will end up with error 409 (Protected branch already exists)
      if (existingBranch.isPresent()) {
        client.unprotectBranchForProject(event.getId(), name);
      }
      if (client.protectBranchForProject(event.getId(), name,
          enforcedSettings.stream().toArray(String[]::new)) != null) {
        eventPublisher.publishEvent(EnforcerEvents.createProjectEvent(execution, "PROTECTED_BRANCH",
            "projectId=" + event.getId(), "projectPath=" + event.getPathWithNamespace(), "branch=" + name));
      }
    }
  }

  private boolean hasRequiredAccessLevels(ProtectedBranch branch) {
    for (int i = 0; i < settings.length; i += 2) {
      if (!branch.hasAccessLevel(settings[i], Permission.fromAccessLevel(settings[i + 1]))) {
        return false;
      }
    }
    return true;
  }

  private boolean hasStricterSettings(ProtectedBranch branch, List<String> enforcedSettings) {
    boolean hasStricterSettings = true;
    for (int i = 0; i < enforcedSettings.size(); i += 2) {
      if (!"unprotect_access_level".equals(enforcedSettings.get(i))) {
        List<AccessLevel> levels = branch.getAccessLevelsByName(enforcedSettings.get(i));
        Permission enforcedPermission = Permission.fromAccessLevel(enforcedSettings.get(i + 1));
        if (levels != null && !levels.isEmpty()) {
          for (AccessLevel level : levels) {
            if (level.getPermission().isStricterOrSame(enforcedPermission)) {
              enforcedSettings.set(i + 1, level.getPermission().getAccessLevel());
            } else {
              hasStricterSettings = false;
            }
          }
        }
      }
    }
    return hasStricterSettings;
  }
}