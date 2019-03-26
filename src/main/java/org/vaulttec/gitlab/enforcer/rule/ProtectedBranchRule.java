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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.client.model.Namespace.Kind;
import org.vaulttec.gitlab.enforcer.client.model.ProtectedBranch;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

public class ProtectedBranchRule implements Rule {

  private static final Logger LOG = LoggerFactory.getLogger(ProtectedBranchRule.class);

  private GitLabClient client;
  private String name;
  private boolean skipUserProjects;
  private String[] settings;

  @Override
  public void init(GitLabClient client, Map<String, String> config) {
    this.client = client;
    this.name = config.get("name");
    if (!StringUtils.hasText(this.name)) {
      throw new IllegalStateException("Missing branch name");
    }
    String skipUserProjectsText = config.get("skipUserProjects");
    if (StringUtils.hasText(skipUserProjectsText)) {
      this.skipUserProjects = Boolean.parseBoolean(skipUserProjectsText);
    }
    this.settings = config.entrySet().stream().filter(e -> e.getKey() != "name" && e.getKey() != "skipUserProjects")
        .flatMap(e -> Arrays.asList(e.getKey(), e.getValue()).stream()).toArray(size -> new String[size]);
  }

  @Override
  public boolean supports(SystemEvent event) {
    return SystemEventName.PROJECT_CREATE.equals(event.getEventName());
  }

  @Override
  public void handle(SystemEvent event) {
    if (!skipUserProjects || client.getProject(event.getId()).getKind() != Kind.USER) {
      LOG.info("Enforcing protected branch '{}' in project '{}'", name, event.getPath());

      // If protected branch already exists then remove it first
      // Otherwise we will end up with error 409 (Protected branch already exists)
      List<ProtectedBranch> branches = client.getProtectedBranchesForProject(event.getId());
      if (branches != null) {
        if (branches.stream().anyMatch(branch -> name.equals(branch.getName()))) {
          client.unprotectBranchForProject(event.getId(), name);
        }
      }
      client.protectBranchForProject(event.getId(), name, settings);
    }
  }
}