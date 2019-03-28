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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaulttec.gitlab.enforcer.client.GitLabClient;

@Configuration
@ConfigurationProperties
public class RulesConfig {
  private final GitLabClient client;
  private final List<RuleConfig> rules; // same name as in config file!!!

  RulesConfig(GitLabClient client) {
    this.client = client;
    this.rules = new ArrayList<>();
  }

  public List<RuleConfig> getRules() {
    return rules;
  }

  @Bean
  public List<Rule> rules() throws InstantiationException, IllegalAccessException {
    List<Rule> result = new ArrayList<>(rules.size());
    for (RuleConfig config : rules) {
      Rule rule = config.getRule().newInstance();
      rule.init(config.getUse(), client, config.getConfig());
      result.add(rule);
    }
    return result;
  }

  protected static class RuleConfig {
    private Class<Rule> rule;
    private Rule.Use use = Rule.Use.ONCE;
    private Map<String, String> config = new HashMap<>();

    public Class<Rule> getRule() {
      return rule;
    }

    public void setRule(Class<Rule> rule) {
      this.rule = rule;
    }

    public Rule.Use getUse() {
      return use;
    }

    public void setUse(Rule.Use use) {
      this.use = use;
    }

    public Map<String, String> getConfig() {
      return config;
    }
  }
}
