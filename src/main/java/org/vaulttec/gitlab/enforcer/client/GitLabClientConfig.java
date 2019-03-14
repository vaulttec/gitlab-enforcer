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

import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.vaulttec.http.client.RestClientConfig;

@Configuration
@ConfigurationProperties(prefix = "gitlab")
public class GitLabClientConfig implements RestClientConfig {
  private final Environment env;
  @NotEmpty
  private String serverUrl;
  @Min(1025)
  @Max(65536)
  private int perPage = 100;
  @NotEmpty
  private String personalAccessToken;

  GitLabClientConfig(Environment env) {
    this.env = env;
  }

  @Override
  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  @Override
  public String getApiPath() {
    return "/api/v4";
  }

  @Override
  public int getPerPage() {
    return perPage;
  }

  public void setPerPage(int perPage) {
    this.perPage = perPage;
  }

  public String getPersonalAccessToken() {
    return personalAccessToken;
  }

  public void setPersonalAccessToken(String personalAccessToken) {
    this.personalAccessToken = personalAccessToken;
  }

  @Override
  public Proxy getProxy() {
    if (StringUtils.hasText(env.getProperty("proxy.host"))) {
      return new Proxy(Proxy.Type.HTTP,
          new InetSocketAddress(env.getProperty("proxy.host"), Integer.parseInt(env.getProperty("proxy.port"))));
    }
    return null;
  }

  @PostConstruct
  public void validate() throws Exception {
    if (StringUtils.hasText(env.getProperty("proxy.host")) && !StringUtils.hasText(env.getProperty("proxy.port"))) {
      throw new IllegalStateException("If proxyHost is defined then proxyPort is required");
    }
  }
}
