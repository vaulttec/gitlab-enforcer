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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.vaulttec.gitlab.enforcer.client.model.Branch;
import org.vaulttec.gitlab.enforcer.client.model.Group;
import org.vaulttec.gitlab.enforcer.client.model.Namespace;
import org.vaulttec.gitlab.enforcer.client.model.Project;
import org.vaulttec.gitlab.enforcer.client.model.ProtectedBranch;
import org.vaulttec.gitlab.enforcer.client.model.PushRules;
import org.vaulttec.http.client.AbstractRestClient;
import org.vaulttec.http.client.LinkHeader;

@Service
public class GitLabClient extends AbstractRestClient {

  private static final Logger LOG = LoggerFactory.getLogger(GitLabClient.class);

  protected static final ParameterizedTypeReference<Group> RESPONSE_TYPE_GROUP = new ParameterizedTypeReference<Group>() {
  };
  protected static final ParameterizedTypeReference<List<Group>> RESPONSE_TYPE_GROUPS = new ParameterizedTypeReference<List<Group>>() {
  };
  protected static final ParameterizedTypeReference<List<Project>> RESPONSE_TYPE_PROJECTS = new ParameterizedTypeReference<List<Project>>() {
  };
  protected static final ParameterizedTypeReference<Project> RESPONSE_TYPE_PROJECT = new ParameterizedTypeReference<Project>() {
  };
  protected static final ParameterizedTypeReference<List<Branch>> RESPONSE_TYPE_BRANCHES = new ParameterizedTypeReference<List<Branch>>() {
  };
  protected static final ParameterizedTypeReference<ProtectedBranch> RESPONSE_TYPE_PROTECTED_BRANCH = new ParameterizedTypeReference<ProtectedBranch>() {
  };
  protected static final ParameterizedTypeReference<List<ProtectedBranch>> RESPONSE_TYPE_PROTECTED_BRANCHES = new ParameterizedTypeReference<List<ProtectedBranch>>() {
  };
  protected static final ParameterizedTypeReference<List<Namespace>> RESPONSE_TYPE_NAMESPACES = new ParameterizedTypeReference<List<Namespace>>() {
  };
  protected static final ParameterizedTypeReference<PushRules> RESPONSE_TYPE_PUSH_RULES = new ParameterizedTypeReference<PushRules>() {
  };

  GitLabClient(GitLabClientConfig config, RestTemplateBuilder restTemplateBuilder) {
    super(config, restTemplateBuilder);
    prepareAuthenticationEntity("PRIVATE-TOKEN", config.getPersonalAccessToken());
  }

  public List<Group> getGroups(String search) {
    LOG.debug("Retrieving groups: search={}", search);
    String apiCall = "/groups";
    Map<String, String> uriVariables = createUriVariables();
    if (StringUtils.hasText(search)) {
      apiCall += "?search={search}";
      uriVariables.put("search", search);
    }
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_GROUPS, uriVariables);
  }

  public Group updateGroup(String groupId, String... settings) {
    if (!StringUtils.hasText(groupId)) {
      throw new IllegalStateException("GitLab group ID required");
    }
    if (settings.length % 2 != 0) {
      throw new IllegalStateException("Key-value required - uneven number of settings");
    }
    String apiCall = "/groups/{groupId}";
    Map<String, String> uriVariables = new HashMap<>();
    for (int i = 0; i < settings.length; i += 2) {
      apiCall += (apiCall.contains("?") ? "&" : "?") + settings[i] + "={" + settings[i] + "}";
      uriVariables.put(settings[i], settings[i + 1]);
    }
    LOG.debug("Updating group '{}': {}", groupId, uriVariables);
    uriVariables.put("groupId", groupId);
    return makeReadApiCall(apiCall, HttpMethod.PUT, RESPONSE_TYPE_GROUP, uriVariables);
  }

  public List<Project> getProjects(String search) {
    LOG.debug("Retrieving projects: search={}", search);
    String apiCall = "/projects";
    Map<String, String> uriVariables = createUriVariables();
    if (StringUtils.hasText(search)) {
      apiCall += "?search={search}";
      uriVariables.put("search", search);
    }
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_PROJECTS, uriVariables);
  }

  public Project getProject(String projectId) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    LOG.debug("Retrieve project '{}'", projectId);
    String apiCall = "/projects/{projectId}";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId);
    return makeReadApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_PROJECT, uriVariables);
  }

  public List<Project> getProjectsForGroup(String groupId, String search) {
    if (!StringUtils.hasText(groupId)) {
      throw new IllegalStateException("GitLab group ID required");
    }
    LOG.debug("Retrieving projects for group '{}': search={}", groupId, search);
    String apiCall = "/groups/{groupId}/projects";
    Map<String, String> uriVariables = createUriVariables("groupId", groupId);
    if (StringUtils.hasText(search)) {
      apiCall += "?search={search}";
      uriVariables.put("search", search);
    }
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_PROJECTS, uriVariables);
  }

  public List<Branch> getBranchesForProject(String projectId, String search) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    LOG.debug("Retrieving branches for project '{}': search={}", projectId, search);
    String apiCall = "/projects/{projectId}/repository/branches";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId);
    if (StringUtils.hasText(search)) {
      apiCall += "?search={search}";
      uriVariables.put("search", search);
    }
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_BRANCHES, uriVariables);
  }

  public ProtectedBranch getProtectedBranchForProject(String projectId, String name) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    LOG.debug("Retrieving protected branches for project '{}'", projectId);
    String apiCall = "/projects/{projectId}/protected_branches/{name}";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId, "name", name);
    return makeReadApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_PROTECTED_BRANCH, uriVariables);
  }

  public List<ProtectedBranch> getProtectedBranchesForProject(String projectId) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    LOG.debug("Retrieving protected branches for project '{}'", projectId);
    String apiCall = "/projects/{projectId}/protected_branches";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId);
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_PROTECTED_BRANCHES, uriVariables);
  }

  public ProtectedBranch protectBranchForProject(String projectId, String name, String... settings) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    if (!StringUtils.hasText(name)) {
      throw new IllegalStateException("Branch name required");
    }
    if (settings.length % 2 != 0) {
      throw new IllegalStateException("Key-value required - uneven number of settings");
    }
    LOG.debug("Protecting branch '{}' for project '{}'", name, projectId);
    String apiCall = "/projects/{projectId}/protected_branches?name={name}";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId, "name", name);
    for (int i = 0; i < settings.length; i += 2) {
      apiCall += (apiCall.contains("?") ? "&" : "?") + settings[i] + "={" + settings[i] + "}";
      uriVariables.put(settings[i], settings[i + 1]);
    }
    return makeReadApiCall(apiCall, HttpMethod.POST, RESPONSE_TYPE_PROTECTED_BRANCH, uriVariables);
  }

  public boolean unprotectBranchForProject(String projectId, String name) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    if (!StringUtils.hasText(name)) {
      throw new IllegalStateException("Branch name required");
    }
    LOG.debug("Unprotecting branch '{}' for project '{}'", name, projectId);
    String apiCall = "/projects/{projectId}/protected_branches/{name}";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId, "name", name);
    return makeWriteApiCall(apiCall, HttpMethod.DELETE, uriVariables);
  }

  @Override
  protected <T> List<T> makeReadListApiCall(String apiCall, HttpMethod method,
      ParameterizedTypeReference<List<T>> typeReference, Map<String, String> uriVariables) {
    String url = getApiUrl(apiCall + (apiCall.contains("?") ? "&" : "?") + "per_page={perPage}");
    uriVariables.put("perPage", perPageAsString());
    try {
      List<T> entities;
      ResponseEntity<List<T>> response = restTemplate.exchange(url, method, authenticationEntity, typeReference,
          uriVariables);
      LinkHeader linkHeader = LinkHeader.parse(response.getHeaders());
      if (linkHeader == null || !linkHeader.hasLink(LinkHeader.Rel.NEXT)) {
        entities = response.getBody();
      } else {
        entities = new ArrayList<>(response.getBody());
        do {
          URI nextResourceUri = linkHeader.getLink(LinkHeader.Rel.NEXT).getResourceUri();
          response = restTemplate.exchange(nextResourceUri, method, authenticationEntity, typeReference);
          entities.addAll(response.getBody());
          linkHeader = LinkHeader.parse(response.getHeaders());
        } while (linkHeader != null && linkHeader.hasLink(LinkHeader.Rel.NEXT));
      }
      return entities;
    } catch (RestClientException e) {
      LOG.error("API call {} '{}' {} failed", method.name(), url, uriVariables, e);
    }
    return null;
  }

  public List<Namespace> getNamespaces(String search) {
    LOG.debug("Retrieving namespaces: search={}", search);
    String apiCall = "/namespaces";
    Map<String, String> uriVariables = createUriVariables();
    if (StringUtils.hasText(search)) {
      apiCall += "?search={search}";
      uriVariables.put("search", search);
    }
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_NAMESPACES, uriVariables);
  }

  public PushRules getPushRules(String projectId) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    LOG.debug("Retrieving push rules for project '{}'", projectId);
    String apiCall = "/projects/{projectId}/push_rule";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId);
    return makeReadApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_PUSH_RULES, uriVariables);
  }

  public PushRules writePushRules(HttpMethod method, String projectId, String... settings) {
    if (!StringUtils.hasText(projectId)) {
      throw new IllegalStateException("GitLab project ID required");
    }
    if (settings.length % 2 != 0) {
      throw new IllegalStateException("Key-value required - uneven number of settings");
    }
    LOG.debug("Updating push rules for project '{}'", projectId);
    String apiCall = "/projects/{projectId}/push_rule?";
    Map<String, String> uriVariables = createUriVariables("projectId", projectId);
    for (int i = 0; i < settings.length; i += 2) {
      apiCall += (apiCall.contains("?") ? "&" : "?") + settings[i] + "={" + settings[i] + "}";
      uriVariables.put(settings[i], settings[i + 1]);
    }
    return makeReadApiCall(apiCall, method, RESPONSE_TYPE_PUSH_RULES, uriVariables);
  }
}
