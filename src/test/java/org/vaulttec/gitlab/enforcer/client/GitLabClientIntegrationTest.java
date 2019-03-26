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
package org.vaulttec.gitlab.enforcer.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.vaulttec.gitlab.enforcer.Application;
import org.vaulttec.gitlab.enforcer.client.model.Branch;
import org.vaulttec.gitlab.enforcer.client.model.Group;
import org.vaulttec.gitlab.enforcer.client.model.Namespace;
import org.vaulttec.gitlab.enforcer.client.model.Permission;
import org.vaulttec.gitlab.enforcer.client.model.Project;
import org.vaulttec.gitlab.enforcer.client.model.ProtectedBranch;

@ActiveProfiles("test")
@IfProfileValue(name = "run.integration.tests", value = "true")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
public class GitLabClientIntegrationTest {
  private static final Logger LOG = LoggerFactory.getLogger(GitLabClientIntegrationTest.class);
  private static final String PROTECTED_BRANCH_NAME = "test-protect-unprotect-branch-for-project";

  @Autowired
  private GitLabClient client;

  @Test
  public void testGetGroups() {
    List<Group> groups = client.getGroups(null);
    assertThat(groups).isNotNull().isNotEmpty();
    for (Group group : groups) {
      LOG.info("{} ({}):", group.getPath(), group.getId());
    }
  }

  @Test
  public void testUpdateGroup() {
    List<Group> groups = client.getGroups(null);
    assertThat(groups).isNotNull().isNotEmpty();
    Group group = groups.get(0);
    LOG.info("{} ({}, {}):", group.getPath(), group.getId(), group.getName());
    String oldName = group.getName();
    group = client.updateGroup(group.getId(), "name", "new-name");
    assertThat(group).isNotNull().hasFieldOrPropertyWithValue("name", "new-name");
    LOG.info("{} ({}, {}):", group.getPath(), group.getId(), group.getName());
    assertThat(client.updateGroup(group.getId(), "name", oldName)).isNotNull().hasFieldOrPropertyWithValue("name",
        oldName);
  }

  @Test
  public void testGetProjects() {
    List<Project> projects = client.getProjects(null);
    assertThat(projects).isNotNull();
    for (Project project : projects) {
      LOG.info("{} ({}, {})", project.getPath(), project.getKind(), project.getId());
    }
  }

  @Test
  public void testGetProject() {
    List<Project> projects = client.getProjects(null);
    assertThat(projects).isNotNull().isNotEmpty();
    Project project = client.getProject(projects.get(0).getId());
    assertThat(project).isNotNull().hasFieldOrPropertyWithValue("name", projects.get(0).getName());
    LOG.info("{} ({}, {})", project.getPath(), project.getKind(), project.getId());
  }

  @Test
  public void testGetProjectsForGroup() {
    List<Group> groups = client.getGroups(null);
    assertThat(groups).isNotNull().isNotEmpty();
    for (Group group : groups) {
      LOG.info("{} ({}):", group.getPath(), group.getId());
      List<Project> projects = client.getProjectsForGroup(group.getId(), null);
      assertThat(projects).isNotNull();
      for (Project project : projects) {
        LOG.info("  {} ({}, {})", project.getPath(), project.getKind(), project.getId());
      }
    }
  }

  @Test
  public void testGetBranchesForProject() {
    List<Group> groups = client.getGroups(null);
    assertThat(groups).isNotNull().isNotEmpty();
    for (Group group : groups) {
      LOG.info("{} ({}):", group.getPath(), group.getId());
      List<Project> projects = client.getProjectsForGroup(group.getId(), null);
      assertThat(projects).isNotNull();
      for (Project project : projects) {
        LOG.info("  {} ({})", project.getPath(), project.getId());
        List<Branch> branches = client.getBranchesForProject(project.getId(), null);
        assertThat(branches).isNotNull();
        for (Branch branch : branches) {
          LOG.info("    {} {}", branch.getName(), branch.isProtected() ? "(PROTECTED)" : "");
        }
      }
    }
  }

  @Test
  public void testGetProtectedBranchesForProject() {
    List<Group> groups = client.getGroups(null);
    assertThat(groups).isNotNull().isNotEmpty();
    for (Group group : groups) {
      LOG.info("{} ({}):", group.getPath(), group.getId());
      List<Project> projects = client.getProjectsForGroup(group.getId(), null);
      assertThat(projects).isNotNull();
      for (Project project : projects) {
        LOG.info("  {} ({})", project.getPath(), project.getId());
        List<ProtectedBranch> branches = client.getProtectedBranchesForProject(project.getId());
        assertThat(branches).isNotNull();
        for (ProtectedBranch branch : branches) {
          LOG.info("    {}: push={}; merge={}; unprotect={}", branch.getName(), branch.getPushAccessLevels(),
              branch.getMergeAccessLevels(), branch.getUnprotectAccessLevels());
        }
      }
    }
  }

  @Test
  public void testProtectGetUnprotectBranchForProject() {
    List<Project> projects = client.getProjects(null);
    assertThat(projects).isNotNull().isNotEmpty();
    Project project = projects.get(0);
    LOG.info("{} ({})", project.getPath(), project.getId());
    ProtectedBranch branch = client.protectBranchForProject(project.getId(), PROTECTED_BRANCH_NAME, "push_access_level",
        Permission.DEVELOPER.getAccessLevel(), "merge_access_level", Permission.MAINTAINER.getAccessLevel(),
        "unprotect_access_level", Permission.ADMIN.getAccessLevel());
    assertThat(branch).isNotNull();
    LOG.info("   {}: push={}; merge={}; unprotect={}", branch.getName(), branch.getPushAccessLevels(),
        branch.getMergeAccessLevels(), branch.getUnprotectAccessLevels());
    branch = client.getProtectedBranchForProject(project.getId(), PROTECTED_BRANCH_NAME);
    assertThat(branch).isNotNull();
    LOG.info("   {}: push={}; merge={}; unprotect={}", branch.getName(), branch.getPushAccessLevels(),
        branch.getMergeAccessLevels(), branch.getUnprotectAccessLevels());
    assertThat(client.unprotectBranchForProject(project.getId(), PROTECTED_BRANCH_NAME)).isTrue();
  }

  @Test
  public void testGetNamespaces() {
    List<Namespace> namespaces = client.getNamespaces(null);
    assertThat(namespaces).isNotNull().isNotEmpty();
    for (Namespace namespace : namespaces) {
      LOG.info("{} ({}, {}):", namespace.getPath(), namespace.getKind(), namespace.getId());
    }
  }
}
