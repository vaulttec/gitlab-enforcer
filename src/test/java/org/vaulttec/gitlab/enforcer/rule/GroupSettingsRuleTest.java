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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.vaulttec.gitlab.enforcer.EnforcerEventPublisher;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.client.model.Group;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventBuilder;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

public class GroupSettingsRuleTest {

  private static final String GROUP_ID = "42";
  private static final String[] GROUP_SETTINGS = new String[] { "membership_lock", "true", "share_with_group_lock",
      "true" };

  private AuditEventRepository eventRepository;
  private EnforcerEventPublisher eventPublisher;
  private GitLabClient client;
  private Map<String, String> config;

  @Before
  public void setUp() throws Exception {
    eventRepository = mock(AuditEventRepository.class);
    eventPublisher = new EnforcerEventPublisher(eventRepository);
    client = mock(GitLabClient.class);
    config = new LinkedHashMap<>();
    config.put("membership_lock", "true");
    config.put("share_with_group_lock", "true");
  }

  @Test
  public void testRuleInfo() {
    Rule rule = new GroupSettingsRule();
    rule.init(null, null, null, config);

    assertThat(rule.getInfo()).endsWith(" (membership_lock=true, share_with_group_lock=true)");
  }

  @Test
  public void testSupports() {
    Rule rule = new GroupSettingsRule();
    assertThat(rule.supports(new SystemEventBuilder().eventName(SystemEventName.PROJECT_CREATE).build())).isFalse();
    assertThat(rule.supports(new SystemEventBuilder().eventName(SystemEventName.GROUP_CREATE).build())).isTrue();
    assertThat(rule.supports(new SystemEventBuilder().eventName(SystemEventName.OTHER).build())).isFalse();
  }

  @Test
  public void testHandle() {
    when(client.updateGroup(GROUP_ID, GROUP_SETTINGS)).thenReturn(new Group());

    Rule rule = new GroupSettingsRule();
    rule.init(null, eventPublisher, client, config);

    rule.handle(EnforcerExecution.HOOK,
        new SystemEventBuilder().eventName(SystemEventName.GROUP_CREATE).id(GROUP_ID).build());
    verify(client).updateGroup(GROUP_ID, GROUP_SETTINGS);
    verify(eventRepository).add(any(AuditEvent.class));
  }
}
