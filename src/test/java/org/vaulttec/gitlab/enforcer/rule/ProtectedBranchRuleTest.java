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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.vaulttec.gitlab.enforcer.EnforcerExecution;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;
import org.vaulttec.gitlab.enforcer.client.model.Namespace;
import org.vaulttec.gitlab.enforcer.client.model.Namespace.Kind;
import org.vaulttec.gitlab.enforcer.client.model.Project;
import org.vaulttec.gitlab.enforcer.client.model.ProtectedBranch;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEvent;
import org.vaulttec.gitlab.enforcer.systemhook.SystemEventName;

public class ProtectedBranchRuleTest {

  private static final String PROJECT_ID = "42";
  private static final String PROJECT_NAME = "project42";
  private static final String BRANCH_NAME = "master";
  private static final String[] BRANCH_SETTINGS = new String[] { "push_access_level", "30", "merge_access_level", "40",
      "unprotect_access_level", "60" };

  private GitLabClient client;
  private Map<String, String> config;

  @Before
  public void setUp() throws Exception {
    client = mock(GitLabClient.class);
    config = new LinkedHashMap<>();
    config.put("name", BRANCH_NAME);
    config.put("push_access_level", "30");
    config.put("merge_access_level", "40");
    config.put("unprotect_access_level", "60");
  }

  @Test
  public void testRuleInfo() {
    config.put("skipUserProjects", "true");

    Rule rule = new ProtectedBranchRule();
    rule.init(null, client, config);

    assertThat(rule.getInfo()).endsWith(
        " (skipUserProject=true, name=master, push_access_level=30, merge_access_level=40, unprotect_access_level=60)");
  }

  @Test
  public void testSupports() {
    Rule rule = new ProtectedBranchRule();
    assertThat(rule.supports(EnforcerExecution.HOOK,
        new SystemEvent(SystemEventName.PROJECT_CREATE, PROJECT_ID, PROJECT_NAME))).isTrue();
    assertThat(rule.supports(EnforcerExecution.HOOK, new SystemEvent(SystemEventName.GROUP_CREATE, null, null)))
        .isFalse();
    assertThat(rule.supports(EnforcerExecution.HOOK, new SystemEvent(SystemEventName.OTHER, null, null))).isFalse();
  }

  @Test
  public void testHandleGroupProjectWithoutExistingBranch() {
    when(client.getProject(PROJECT_ID))
        .thenReturn(new Project(PROJECT_ID, PROJECT_NAME, new Namespace("1", "ns1", Kind.GROUP)));

    Rule rule = new ProtectedBranchRule();
    rule.init(null, client, config);

    rule.handle(new SystemEvent(SystemEventName.PROJECT_CREATE, PROJECT_ID, PROJECT_NAME));
    verify(client).getProtectedBranchesForProject(PROJECT_ID);
    verify(client, never()).unprotectBranchForProject(PROJECT_ID, BRANCH_NAME);
    verify(client).protectBranchForProject(PROJECT_ID, BRANCH_NAME, BRANCH_SETTINGS);
  }

  @Test
  public void testHandleGroupProjectWithExistingBranchWithSameSettings() {
    when(client.getProject(PROJECT_ID))
        .thenReturn(new Project(PROJECT_ID, PROJECT_NAME, new Namespace("1", "ns1", Kind.GROUP)));
    when(client.getProtectedBranchesForProject(PROJECT_ID))
        .thenReturn(Arrays.asList(new ProtectedBranch(BRANCH_NAME, BRANCH_SETTINGS)));

    Rule rule = new ProtectedBranchRule();
    rule.init(null, client, config);

    rule.handle(new SystemEvent(SystemEventName.PROJECT_CREATE, PROJECT_ID, PROJECT_NAME));
    verify(client).getProtectedBranchesForProject(PROJECT_ID);
    verify(client, never()).unprotectBranchForProject(PROJECT_ID, BRANCH_NAME);
    verify(client, never()).protectBranchForProject(PROJECT_ID, BRANCH_NAME, BRANCH_SETTINGS);
  }

  @Test
  public void testHandleGroupProjectWithExistingBranchWithDifferentSettings() {
    when(client.getProject(PROJECT_ID))
        .thenReturn(new Project(PROJECT_ID, PROJECT_NAME, new Namespace("1", "ns1", Kind.GROUP)));
    when(client.getProtectedBranchesForProject(PROJECT_ID)).thenReturn(Arrays.asList(new ProtectedBranch(BRANCH_NAME)));

    Rule rule = new ProtectedBranchRule();
    rule.init(null, client, config);

    rule.handle(new SystemEvent(SystemEventName.PROJECT_CREATE, PROJECT_ID, PROJECT_NAME));
    verify(client).getProtectedBranchesForProject(PROJECT_ID);
    verify(client).unprotectBranchForProject(PROJECT_ID, BRANCH_NAME);
    verify(client).protectBranchForProject(PROJECT_ID, BRANCH_NAME, BRANCH_SETTINGS);
  }

  @Test
  public void testHandleUserProjectExistingBranchWithDifferentSettings() {
    when(client.getProject(PROJECT_ID))
        .thenReturn(new Project(PROJECT_ID, PROJECT_NAME, new Namespace("1", "ns1", Kind.USER)));
    when(client.getProtectedBranchesForProject(PROJECT_ID)).thenReturn(Arrays.asList(new ProtectedBranch(BRANCH_NAME)));

    Rule rule = new ProtectedBranchRule();
    rule.init(null, client, config);

    rule.handle(new SystemEvent(SystemEventName.PROJECT_CREATE, PROJECT_ID, PROJECT_NAME));
    verify(client).getProtectedBranchesForProject(PROJECT_ID);
    verify(client).unprotectBranchForProject(PROJECT_ID, BRANCH_NAME);
    verify(client).protectBranchForProject(PROJECT_ID, BRANCH_NAME, BRANCH_SETTINGS);
  }

  @Test
  public void testHandleUserProjectWithSkipUserProjects() {
    when(client.getProject(PROJECT_ID))
        .thenReturn(new Project(PROJECT_ID, PROJECT_NAME, new Namespace("1", "ns1", Kind.USER)));

    config.put("skipUserProjects", "true");
    Rule rule = new ProtectedBranchRule();
    rule.init(null, client, config);

    rule.handle(new SystemEvent(SystemEventName.PROJECT_CREATE, PROJECT_ID, PROJECT_NAME));
    verify(client).getProject(PROJECT_ID);
    verify(client, never()).getProtectedBranchesForProject(PROJECT_ID);
    verify(client, never()).unprotectBranchForProject(PROJECT_ID, BRANCH_NAME);
    verify(client, never()).protectBranchForProject(PROJECT_ID, BRANCH_NAME);
  }

  @Test
  public void testHandleGroupProjectWithSkipUserProjects() {
    when(client.getProject(PROJECT_ID))
        .thenReturn(new Project(PROJECT_ID, PROJECT_NAME, new Namespace("1", "ns1", Kind.GROUP)));

    config.put("skipUserProjects", "true");
    Rule rule = new ProtectedBranchRule();
    rule.init(null, client, config);

    rule.handle(new SystemEvent(SystemEventName.PROJECT_CREATE, PROJECT_ID, PROJECT_NAME));
    verify(client).getProject(PROJECT_ID);
    verify(client).getProtectedBranchesForProject(PROJECT_ID);
    verify(client, never()).unprotectBranchForProject(PROJECT_ID, BRANCH_NAME);
    verify(client).protectBranchForProject(PROJECT_ID, BRANCH_NAME, BRANCH_SETTINGS);
  }
}
