GitLab Enforcer  [![Build Status](https://travis-ci.org/vaulttec/gitlab-enforcer.svg?branch=master)](https://travis-ci.org/vaulttec/gitlab-enforcer) [![Docker Image](https://img.shields.io/docker/pulls/tjuerge/gitlab-enforcer.svg)](https://hub.docker.com/r/tjuerge/gitlab-enforcer)
===============

Spring Boot application with [GitLab System Hook](https://docs.gitlab.com/ee/system_hooks/system_hooks.html)
listener which enforces certain configuration rules on newly created GitLab groups and projects:

 * Group
   - [Share with group lock](https://docs.gitlab.com/ce/user/group/index.html#share-with-group-lock)
   - [Member Lock](https://docs.gitlab.com/ee/user/group/index.html#member-lock-starter)
   - [Auto DevOps](https://docs.gitlab.com/ee/topics/autodevops/)
   - [Default project-creation level](https://docs.gitlab.com/ee/user/group/index.html#default-project-creation-level)
   - [Default subgroup-creation level](https://docs.gitlab.com/ee/user/group/subgroups/#creating-a-subgroup)
 * Project
   - [Protected Branches](https://docs.gitlab.com/ee/user/project/protected_branches.html)

These rules are defined in [`src/main/resources/config/application.yml`](src/main/resources/config/application.yml)
```
rules:
  - rule: org.vaulttec.gitlab.enforcer.rule.GroupSettingsRule
    use: once
    config:
      membership_lock: true
      share_with_group_lock: true
      auto_devops_enabled: false
      project_creation_level: maintainer
      subgroup_creation_level: owner
  - rule: org.vaulttec.gitlab.enforcer.rule.ProtectedBranchRule
    use: always
    config:
      skipUserProjects: true
      keepStricterAccessLevel: true
      name: master
      push_access_level: 30
      merge_access_level: 30
  - rule: org.vaulttec.gitlab.enforcer.rule.ProtectedBranchRule
    use: always
    config:
      skipUserProjects: true
      keepStricterAccessLevel: true
      name: release/*
      push_access_level: 40
      merge_access_level: 40
```

All the rules marked with the configuration property `use: always` are automatically re-enforced at a specified interval (msec)
```
enforcer:
  scheduler:
    enabled: true
    rate: 300000  # 5 min
```
 

## Install Maven Wrapper
```
cd /path/to/project
mvn -N io.takari:maven:wrapper
```

## Run the project with

```
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=test
```

Open browser to http://localhost:8080/


## To package the project run

```
./mvnw clean package
```
