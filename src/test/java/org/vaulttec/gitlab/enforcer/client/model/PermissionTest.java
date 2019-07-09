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
package org.vaulttec.gitlab.enforcer.client.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PermissionTest {

  @Test
  public void test() {
    assertTrue(Permission.ADMIN.isStricter(Permission.MAINTAINER));
    assertTrue(Permission.MAINTAINER.isStricter(Permission.DEVELOPER));
    assertTrue(Permission.DEVELOPER.isStricter(Permission.GUEST));
    assertTrue(Permission.NO.isStricter(Permission.MAINTAINER));
    assertTrue(Permission.NO.isStricter(Permission.DEVELOPER));

    assertFalse(Permission.MAINTAINER.isStricter(Permission.MAINTAINER));
    assertFalse(Permission.NO.isStricter(Permission.NO));
    assertFalse(Permission.DEVELOPER.isStricter(Permission.NO));
  }
}
