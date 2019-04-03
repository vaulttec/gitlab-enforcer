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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnProperty(prefix = "enforcer.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class EnforcerScheduler {

  private final EnforcerClient client;

  public EnforcerScheduler(EnforcerClient client) {
    this.client = client;
  }

  @Scheduled(fixedRateString = "${enforcer.scheduler.rate}")
  public void enforceScheduled() {
    client.enforce(EnforcerExecution.SCHEDULED);
  }
}