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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.vaulttec.gitlab.enforcer.EnforcerEvents;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;
import org.vaulttec.gitlab.enforcer.client.model.Namespace.Kind;
import org.vaulttec.gitlab.enforcer.client.model.Project;
import org.vaulttec.gitlab.enforcer.client.model.PushRules;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PushRulesRule extends AbstractRule {

  private static final Logger LOG = LoggerFactory.getLogger(PushRulesRule.class);

  private boolean skipUserProjects;
  private String[] settings;
  private String[] settingsInfo;

  @Override
  public String getInfo() {
    StringBuilder info = new StringBuilder("Enforce Push Rules");
    if (settingsInfo != null) {
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
    String skipUserProjectsText = config.get("skipUserProjects");
    if (StringUtils.hasText(skipUserProjectsText)) {
      this.skipUserProjects = Boolean.parseBoolean(skipUserProjectsText);
    }
    this.settings = config.entrySet().stream().filter(e -> !"skipUserProjects".equals(e.getKey()))
        .flatMap(e -> Stream.of(e.getKey(), e.getValue())).toArray(String[]::new);
    List<String> settingsList = new ArrayList<>();
    settingsList.add("skipUserProjects=" + skipUserProjects);
    for (int i = 0; i < settings.length; i += 2) {
      settingsList.add(settings[i] + "=" + settings[i + 1]);
    }
    this.settingsInfo = settingsList.toArray(new String[0]);
  }

  @Override
  public void doHandle(EnforcerExecution execution, SystemEvent event) {
    if (!skip(event)) {
      PushRules rules = client.getPushRules(event.getId());
      if (rules == null || !rules.isActiveSettings(settings)) {
        HttpMethod method = (rules == null ? HttpMethod.POST : HttpMethod.PUT); 
        LOG.info("Enforcing push rules in project '{}'", event.getPathWithNamespace());
        if (client.writePushRules(method, event.getId(), settings) != null) {
          eventPublisher.publishEvent(EnforcerEvents.createProjectEvent(execution, "PUSH_RULES",
              "projectId=" + event.getId(), "projectPath=" + event.getPathWithNamespace()));
        }
      }
    }
  }

  private boolean skip(SystemEvent event) {
    Project project = event.getObject() != null ? (Project) event.getObject() : client.getProject(event.getId());
    if (project.isRepositoryDisabled()) {
      return true;
    }

    return skipUserProjects &&project.getKind() == Kind.USER;
  }
}