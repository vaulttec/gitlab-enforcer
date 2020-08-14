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
package org.vaulttec.gitlab.enforcer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.client.model.Group;
import org.vaulttec.gitlab.enforcer.client.model.Project;
import org.vaulttec.gitlab.enforcer.rule.Rule;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventBuilder;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

@Service
public class EnforcerClient {

  private static final Logger LOG = LoggerFactory.getLogger(EnforcerClient.class);

  private GitLabClient client;
  private List<Rule> rules;
  private Instant lastEnforceTime;

  public EnforcerClient(GitLabClient client, List<Rule> rules) {
    this.client = client;
    this.rules = rules;
  }

  public List<String> getRulesInfo() {
    List<String> rulesInfo = new ArrayList<>();
    for (Rule rule : rules) {
      rulesInfo.add(rule.getInfo());
    }
    return rulesInfo;
  }

  public Instant getLastEnforceTime() {
    return lastEnforceTime;
  }

  public void enforce(EnforcerExecution execution) {
    LOG.info("Enforcing rules for all GitLab groups and projects ({})", execution);
    List<Group> groups = client.getGroups(null);
    if (groups != null) {
      groups.forEach(group -> {
        SystemEvent event = new SystemEventBuilder().eventName(SystemEventName.GROUP_CREATE).id(group.getId())
            .object(group).name(group.getName()).path(group.getPath()).build();
        enforce(execution, event);
      });
    }
    List<Project> projects = client.getProjects(null);
    if (projects != null) {
      projects.forEach(project -> {
        SystemEvent event = new SystemEventBuilder().eventName(SystemEventName.PROJECT_CREATE).id(project.getId())
            .object(project).name(project.getName()).path(project.getPath())
            .pathWithNamespace(project.getPathWithNamespace()).build();
        enforce(execution, event);
      });
    }
    lastEnforceTime = Instant.now();
  }

  public void enforce(EnforcerExecution execution, SystemEvent event) {
    rules.forEach(rule -> {
      if (rule.supports(event)) {
        rule.handle(execution, event);
      }
    });
  }
}
