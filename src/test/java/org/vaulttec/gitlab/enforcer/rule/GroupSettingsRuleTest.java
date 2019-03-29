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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

public class GroupSettingsRuleTest {

  private static final String GROUP_ID = "42";
  private static final String GROUP_NAME = "group42";

  private GitLabClient client;
  private Map<String, String> config;

  @Before
  public void setUp() throws Exception {
    client = mock(GitLabClient.class);
    config = new LinkedHashMap<>();
    config.put("membership_lock", "true");
    config.put("share_with_group_lock", "true");
  }

  @Test
  public void testRuleInfo() {
    Rule rule = new GroupSettingsRule();
    rule.init(null, null, config);

    assertThat(rule.getInfo()).endsWith(" (membership_lock=true, share_with_group_lock=true)");
  }

  @Test
  public void testSupports() {
    Rule rule = new GroupSettingsRule();
    assertThat(rule.supports(new SystemEvent(SystemEventName.GROUP_CREATE, GROUP_ID, GROUP_NAME))).isTrue();
    assertThat(rule.supports(new SystemEvent(SystemEventName.PROJECT_CREATE, null, null))).isFalse();
    assertThat(rule.supports(new SystemEvent(SystemEventName.OTHER, null, null))).isFalse();
  }

  @Test
  public void testHandle() {
    Rule rule = new GroupSettingsRule();
    rule.init(null, client, config);

    rule.handle(EnforcerExecution.HOOK, new SystemEvent(SystemEventName.GROUP_CREATE, GROUP_ID, GROUP_NAME));
    verify(client).updateGroup(GROUP_ID, "membership_lock", "true", "share_with_group_lock", "true");
  }
}
