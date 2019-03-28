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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.vaulttec.gitlab.enforcer.EnforcerClient;
import org.vaulttec.gitlab.enforcer.EnforcerConfig;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;

@RestController
public class SystemHooksController {

  private static final Logger LOG = LoggerFactory.getLogger(SystemHooksController.class);

  private EnforcerClient client;
  private EnforcerConfig config;

  public SystemHooksController(EnforcerClient client, EnforcerConfig config) {
    this.client = client;
    this.config = config;
  }

  @PostMapping(value = "/systemhooks", consumes = "application/json")
  public void process(@RequestHeader(name = "X-Gitlab-Event", required = true) String header,
      @RequestHeader(name = "X-Gitlab-Token", required = true) String token, @RequestBody SystemEvent event) {
    if (StringUtils.hasText(config.getSystemHookToken()) && !config.getSystemHookToken().equals(token)) {
      LOG.warn("Unexpected token '{}' - ignoring {} event '{}'", token, header, event.getEventName());
    } else {
      if (event.getEventName() != SystemEventName.OTHER) {
        LOG.info("Processing {} event '{}'", header, event.getEventName());
        client.enforce(EnforcerExecution.HOOK, event);
      }
    }
  }
}
