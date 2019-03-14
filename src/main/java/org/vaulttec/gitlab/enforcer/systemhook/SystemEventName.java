package org.vaulttec.gitlab.enforcer.systemhook;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SystemEventName {
  PROJECT_CREATE("project_create"), GROUP_CREATE("group_create"), OTHER("other");

  private final static Map<String, SystemEventName> ENUM_NAME_MAP;
  static {
    ENUM_NAME_MAP = Arrays.stream(SystemEventName.values())
        .collect(Collectors.toMap(SystemEventName::getName, Function.identity()));
  }

  @JsonCreator
  public static SystemEventName fromName(String name) {
    return ENUM_NAME_MAP.get(name) != null ? ENUM_NAME_MAP.get(name) : OTHER;
  }

  String name;

  private SystemEventName(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }
}
