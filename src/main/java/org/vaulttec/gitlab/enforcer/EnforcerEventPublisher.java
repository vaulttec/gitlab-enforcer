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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EnforcerEventPublisher {
  private static final Logger AUDIT_LOG = LoggerFactory.getLogger("AUDIT_LOG");
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final AuditEventRepository eventRepository;

  public EnforcerEventPublisher(AuditEventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public void publishEvent(AuditEvent event) {
    try {
      AUDIT_LOG.trace(MAPPER.writeValueAsString(event));
    } catch (JsonProcessingException e) {
      AUDIT_LOG.error("Error serializing audit event: {}", event, e);
    }
    if (eventRepository != null) {
      eventRepository.add(event);
    }
  }
}