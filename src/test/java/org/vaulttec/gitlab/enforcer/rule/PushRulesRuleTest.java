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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.http.HttpMethod;
import org.vaulttec.gitlab.enforcer.EnforcerEventPublisher;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.client.model.Namespace;
import org.vaulttec.gitlab.enforcer.client.model.Namespace.Kind;
import org.vaulttec.gitlab.enforcer.client.model.Project;
import org.vaulttec.gitlab.enforcer.client.model.PushRules;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventBuilder;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PushRulesRuleTest {

  private static final String PROJECT_ID = "42";
  private static final Project GROUP_PROJECT = new Project(PROJECT_ID, null, new Namespace("1", "ns1", Kind.GROUP));
  private static final String[] PUSH_RULES_SETTINGS = new String[] { "member_check", "true", "prevent_secrets",
      "true" };

  private AuditEventRepository eventRepository;
  private EnforcerEventPublisher eventPublisher;
  private GitLabClient client;
  private Map<String, String> config;
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setUp() {
    eventRepository = mock(AuditEventRepository.class);
    objectMapper = mock(ObjectMapper.class);
    eventPublisher = new EnforcerEventPublisher(eventRepository, objectMapper);
    client = mock(GitLabClient.class);
    config = new LinkedHashMap<>();
    config.put("member_check", "true");
    config.put("prevent_secrets", "true");
  }

  @Test
  public void testRuleInfo() {
    config.put("skipUserProjects", "true");

    Rule rule = new PushRulesRule();
    rule.init(null, null, null, config);

    assertThat(rule.getInfo()).endsWith(" (skipUserProjects=true, member_check=true, prevent_secrets=true)");
  }

  @Test
  public void testSupports() {
    Rule rule = new PushRulesRule();
    assertThat(rule.supports(new SystemEventBuilder().eventName(SystemEventName.PROJECT_CREATE).build())).isTrue();
    assertThat(rule.supports(new SystemEventBuilder().eventName(SystemEventName.GROUP_CREATE).build())).isFalse();
    assertThat(rule.supports(new SystemEventBuilder().eventName(SystemEventName.OTHER).build())).isFalse();
  }

  @Test
  public void testHandleAdd() {
    when(client.getProject(PROJECT_ID)).thenReturn(GROUP_PROJECT);
    when(client.getPushRules(PROJECT_ID)).thenReturn(null);
    when(client.writePushRules(HttpMethod.POST, PROJECT_ID, PUSH_RULES_SETTINGS)).thenReturn(new PushRules());

    Rule rule = new PushRulesRule();
    rule.init(null, eventPublisher, client, config);

    rule.handle(EnforcerExecution.HOOK,
        new SystemEventBuilder().eventName(SystemEventName.GROUP_CREATE).id(PROJECT_ID).build());
    verify(client).getPushRules(PROJECT_ID);
    verify(client).writePushRules(HttpMethod.POST, PROJECT_ID, PUSH_RULES_SETTINGS);
    verify(eventRepository).add(any(AuditEvent.class));
  }

  @Test
  public void testHandleUpdate() {
    when(client.getProject(PROJECT_ID)).thenReturn(GROUP_PROJECT);
    PushRules existingRules = new PushRules();
    existingRules.setMemberCheck(false);
    when(client.getPushRules(PROJECT_ID)).thenReturn(existingRules);
    when(client.writePushRules(HttpMethod.PUT, PROJECT_ID, PUSH_RULES_SETTINGS)).thenReturn(existingRules);

    Rule rule = new PushRulesRule();
    rule.init(null, eventPublisher, client, config);

    rule.handle(EnforcerExecution.HOOK,
        new SystemEventBuilder().eventName(SystemEventName.GROUP_CREATE).id(PROJECT_ID).build());
    verify(client).getPushRules(PROJECT_ID);
    verify(client).writePushRules(HttpMethod.PUT, PROJECT_ID, PUSH_RULES_SETTINGS);
    verify(eventRepository).add(any(AuditEvent.class));
  }

  @Test
  public void testHandleSkipActiveSettings() {
    when(client.getProject(PROJECT_ID)).thenReturn(GROUP_PROJECT);
    PushRules existingRules = new PushRules();
    existingRules.setMemberCheck(true);
    existingRules.setPreventSecrets(true);
    when(client.getPushRules(PROJECT_ID)).thenReturn(existingRules);

    Rule rule = new PushRulesRule();
    rule.init(null, eventPublisher, client, config);

    rule.handle(EnforcerExecution.HOOK,
        new SystemEventBuilder().eventName(SystemEventName.GROUP_CREATE).id(PROJECT_ID).build());
    verify(client).getPushRules(PROJECT_ID);
    verify(client, never()).writePushRules(HttpMethod.PUT, PROJECT_ID, PUSH_RULES_SETTINGS);
    verify(eventRepository, never()).add(any(AuditEvent.class));
  }

  @Test
  public void testHandleUserProjectWithSkipUserProjects() {
    when(client.getProject(PROJECT_ID)).thenReturn(new Project(PROJECT_ID, null, new Namespace("1", "ns1", Kind.USER), null));

    config.put("skipUserProjects", "true");
    Rule rule = new PushRulesRule();
    rule.init(null, eventPublisher, client, config);

    rule.handle(EnforcerExecution.HOOK, new SystemEventBuilder().id(PROJECT_ID).build());
    verify(client).getProject(PROJECT_ID);
    verify(client, never()).getPushRules(PROJECT_ID);
    verify(client, never()).writePushRules(HttpMethod.PUT, PROJECT_ID, PUSH_RULES_SETTINGS);
    verify(eventRepository, never()).add(any(AuditEvent.class));
  }

  @Test
  public void testHandleProjectWithoutGitRepository() {
    Project project = new Project(PROJECT_ID, null, new Namespace("1", "ns1", Kind.GROUP));
    project.setRepositoryAccessLevel("disabled");
    when(client.getProject(PROJECT_ID)).thenReturn(project);

    Rule rule = new PushRulesRule();
    rule.init(null, eventPublisher, client, config);

    rule.handle(EnforcerExecution.HOOK, new SystemEventBuilder().id(PROJECT_ID).build());
    verify(client).getProject(PROJECT_ID);
    verify(client, never()).getPushRules(PROJECT_ID);
    verify(client, never()).writePushRules(HttpMethod.PUT, PROJECT_ID, PUSH_RULES_SETTINGS);
    verify(eventRepository, never()).add(any(AuditEvent.class));
  }
}
