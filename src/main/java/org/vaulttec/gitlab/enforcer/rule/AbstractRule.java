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

import java.util.Map;

import org.vaulttec.gitlab.enforcer.EnforcerEventPublisher;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;

public abstract class AbstractRule implements Rule {
  protected Use use;
  protected GitLabClient client;
  protected EnforcerEventPublisher eventPublisher;

  @Override
  public final void init(Use use, EnforcerEventPublisher eventPublisher, GitLabClient client,
      Map<String, String> config) {
    this.use = use;
    this.eventPublisher = eventPublisher;
    this.client = client;
    doInit(config);
  }

  protected abstract void doInit(Map<String, String> config);

  @Override
  public final void handle(EnforcerExecution execution, SystemEvent event) {
    switch (execution) {
    case COMMAND:
    case HOOK:
      doHandle(execution, event);
      break;
    case SCHEDULED:
      if (use == Use.ALWAYS) {
        doHandle(execution, event);
      }
      break;
    }
  }

  protected abstract void doHandle(EnforcerExecution execution, SystemEvent event);
}