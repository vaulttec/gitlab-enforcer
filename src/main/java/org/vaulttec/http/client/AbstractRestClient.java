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
package org.vaulttec.http.client;

import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractRestClient {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractRestClient.class);

  protected final RestClientConfig config;
  protected final RestTemplate restTemplate;
  protected HttpEntity<String> authenticationEntity;

  public AbstractRestClient(RestClientConfig config, RestTemplateBuilder restTemplateBuilder) {
    this.config = config;
    this.restTemplate = createRestTemplate(restTemplateBuilder, config.getProxy());
  }

  private RestTemplate createRestTemplate(RestTemplateBuilder restTemplateBuilder, Proxy proxy) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    if (proxy != null) {
      SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
      requestFactory.setProxy(proxy);
      restTemplate.setRequestFactory(requestFactory);
    }
    return restTemplate;
  }

  protected String perPageAsString() {
    return Integer.toString(config.getPerPage());
  }

  protected String getApiUrl(String apiCall) {
    return config.getServerUrl() + config.getApiPath() + apiCall;
  }

  protected Map<String, String> createUriVariables(String... variables) {
    if (variables.length % 2 != 0) {
      throw new IllegalStateException("Key-value required - uneven number of arguments");
    }
    Map<String, String> uriVariables = new HashMap<>();
    for (int i = 0; i < variables.length; i += 2) {
      uriVariables.put(variables[i], variables[i + 1]);
    }
    return uriVariables;
  }

  protected void prepareAuthenticationEntity(String headerName, String headerValue) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(headerName, headerValue);
    authenticationEntity = new HttpEntity<String>(headers);
  }

  protected <T> T makeReadApiCall(String apiCall, HttpMethod method, ParameterizedTypeReference<T> typeReference,
      Map<String, String> uriVariables) {
    String url = getApiUrl(apiCall);
    try {
      ResponseEntity<T> response = restTemplate.exchange(url, method, authenticationEntity, typeReference,
          uriVariables);
      return response.getBody();
    } catch (Exception e) {
      logException(method, uriVariables, url, e);
    }
    return null;
  }

  protected <T> List<T> makeReadListApiCall(String apiCall, HttpMethod method,
      ParameterizedTypeReference<List<T>> typeReference, Map<String, String> uriVariables) {
    String url = getApiUrl(apiCall);
    try {
      ResponseEntity<List<T>> response = restTemplate.exchange(url, method, authenticationEntity, typeReference,
          uriVariables);
      return response.getBody();
    } catch (Exception e) {
      logException(method, uriVariables, url, e);
    }
    return null;
  }

  protected boolean makeWriteApiCall(String apiCall, HttpMethod method, Map<String, String> uriVariables) {
    String url = getApiUrl(apiCall);
    try {
      restTemplate.exchange(url, method, authenticationEntity, Void.class, uriVariables);
      return true;
    } catch (Exception e) {
      logException(method, uriVariables, url, e);
    }
    return false;
  }

  private void logException(HttpMethod method, Map<String, String> uriVariables, String url, Exception e) {
    if (e instanceof RestClientResponseException) {
      LOG.error("API call {} '{}' {} failed with {}: {}", method.name(), url, uriVariables, e.getMessage(),
          ((RestClientResponseException) e).getResponseBodyAsString());
    } else if (e instanceof RestClientException) {
      LOG.error("API call {} '{}' {} failed with {}", method.name(), url, uriVariables, e.getMessage());
    } else {
      LOG.error("API call {} '{}' {} failed", method.name(), url, uriVariables, e);
    }
  }
}