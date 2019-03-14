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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.vaulttec.gitlab.enforcer.EnforcerConfig;
import org.vaulttec.gitlab.enforcer.rule.Rule;

@RestController
public class SystemHooksController {

  private static final Logger LOG = LoggerFactory.getLogger(SystemHooksController.class);

  private EnforcerConfig config;
  private List<Rule> rules;

  public SystemHooksController(EnforcerConfig config, List<Rule> rules) {
    this.config = config;
    this.rules = rules;
  }

  @PostMapping(value = "/systemhooks", consumes = "application/json")
  public void process(@RequestHeader(name = "X-Gitlab-Event", required = true) String header,
      @RequestHeader(name = "X-Gitlab-Token", required = true) String token, @RequestBody SystemEvent event) {
    if (config.getSystemHookToken().equals(token) && event.getEventName() != SystemEventName.OTHER) {
      LOG.info("Processing {} event '{}'", header, event.getEventName());
      rules.forEach(rule -> {
        if (rule.supports(event)) {
          rule.handle(event);
        }
      });
    }
  }
}
