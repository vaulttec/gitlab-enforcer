GitLab Enforcer
===============

Spring Boot application with [GitLab System Hook](https://docs.gitlab.com/ee/system_hooks/system_hooks.html)
listener which enforces certain configuration rules on newly created GitLab groups and projects:

 * Group
   - [Share with group lock](https://docs.gitlab.com/ce/user/group/index.html#share-with-group-lock)
   - [Member Lock](https://docs.gitlab.com/ee/user/group/index.html#member-lock-starter)
 * Project
   - [Protected Branches](https://docs.gitlab.com/ee/user/project/protected_branches.html)

These rules are defined in [`src/main/resources/config/application.yml`](src/main/resources/config/application.yml)
```
rules:
  - rule: org.vaulttec.gitlab.enforcer.rule.GroupSettingsRule
    config:
      membership_lock: true
      share_with_group_lock: true
  - rule: org.vaulttec.gitlab.enforcer.rule.ProtectedBranchRule
    config:
      name: master
      push_access_level: 30
      merge_access_level: 40
      unprotect_access_level: 60
  - rule: org.vaulttec.gitlab.enforcer.rule.ProtectedBranchRule
    config:
      name: release/*
      push_access_level: 40
      merge_access_level: 40
      unprotect_access_level: 60
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
