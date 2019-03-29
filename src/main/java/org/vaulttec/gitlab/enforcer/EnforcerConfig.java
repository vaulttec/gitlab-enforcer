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

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "enforcer")
public class EnforcerConfig {
  private int auditEventRepositoryCapacity;
  @NotEmpty
  private String systemHookToken;

  public int getAuditEventRepositoryCapacity() {
    return auditEventRepositoryCapacity;
  }

  public void setAuditEventRepositoryCapacity(int auditEventRepositoryCapacity) {
    this.auditEventRepositoryCapacity = auditEventRepositoryCapacity;
  }

  public String getSystemHookToken() {
    return systemHookToken;
  }

  public void setSystemHookToken(String systemHookToken) {
    this.systemHookToken = systemHookToken;
  }

  @Bean
  public AuditEventRepository auditEventRepository() throws Exception {
    return new InMemoryAuditEventRepository(getAuditEventRepositoryCapacity());
  }
}