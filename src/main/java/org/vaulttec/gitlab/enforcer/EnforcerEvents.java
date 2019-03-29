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

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Service;

@Service
public class EnforcerEvents {

  public static final String PRINCIPAL = "gitlab-enforcer";

  public static final String GROUP_UPDATED = "GROUP_UPDATED";
  public static final String PROJECT_UPDATED = "PROJECT_UPDATED";

  public static AuditEvent createGroupEvent(EnforcerExecution execution, String rule, String... data) {
    return createEvent(GROUP_UPDATED, execution, rule, data);
  }

  public static AuditEvent createProjectEvent(EnforcerExecution execution, String rule, String... data) {
    return createEvent(PROJECT_UPDATED, execution, rule, data);
  }

  private static AuditEvent createEvent(String type, EnforcerExecution execution, String rule, String... data) {
    Map<String, Object> dataMap = convertToMap(data);
    dataMap.put("execution", execution);
    dataMap.put("rule", rule);
    return new AuditEvent(PRINCIPAL, type, dataMap);
  }

  private static Map<String, Object> convertToMap(String[] data) {
    Map<String, Object> map = new HashMap<>();
    for (String entry : data) {
      int index = entry.indexOf('=');
      if (index != -1) {
        map.put(entry.substring(0, index), entry.substring(index + 1));
      } else {
        map.put(entry, null);
      }
    }
    return map;
  }
}