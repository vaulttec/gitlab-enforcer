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

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class EnforcerInfoContributor implements InfoContributor {

  @Autowired
  private EnforcerClient enforcerTask;

  @Override
  public void contribute(Builder builder) {
    Map<String, Object> enforcerDetails = new HashMap<>();
    enforcerDetails.put("rules", enforcerTask.getRulesInfo());
    enforcerDetails.put("lastEnforce", enforcerTask.getLastEnforceTime());
    builder.withDetail("enforcer", enforcerDetails);
  }
}
