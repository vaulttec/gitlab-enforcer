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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

public class GroupSettingsRule implements Rule {

  private static final Logger LOG = LoggerFactory.getLogger(GroupSettingsRule.class);

  private GitLabClient client;
  private String[] settings;

  @Override
  public void init(GitLabClient client, Map<String, String> config) {
    this.client = client;
    this.settings = config.entrySet().stream().flatMap(e -> Arrays.asList(e.getKey(), e.getValue()).stream())
        .toArray(size -> new String[size]);
  }

  @Override
  public boolean supports(SystemEvent event) {
    return SystemEventName.GROUP_CREATE.equals(event.getEventName());
  }

  @Override
  public void handle(SystemEvent event) {
    LOG.info("Enforcing settings in group '{}'", event.getPath());
    client.updateGroup(event.getId(), settings);
  }
}