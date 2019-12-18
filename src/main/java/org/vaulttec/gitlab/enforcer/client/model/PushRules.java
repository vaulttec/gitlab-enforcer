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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PushRules {
  private String id;
  @JsonProperty("created_at")
  private String createdAt;
  @JsonProperty("commit_message_regex")
  private String commitMessageRegex;
  @JsonProperty("commit_message_negative_regex")
  private String commitMessageNegativeRegex;
  @JsonProperty("branch_name_regex")
  private String branchNameRegex;
  @JsonProperty("deny_delete_tag")
  private boolean denyDeleteTag;
  @JsonProperty("member_check")
  private boolean memberCheck;
  @JsonProperty("prevent_secrets")
  private boolean preventSecrets;
  @JsonProperty("author_email_regex")
  private String authorEmailRegex;
  @JsonProperty("file_name_regex")
  private String fileNameRegex;
  @JsonProperty("max_file_size")
  private Integer maxFileSize;
  @JsonProperty("commit_committer_check")
  private boolean commitCommitterCheck;
  @JsonProperty("reject_unsigned_commits")
  private boolean rejectUnsignedCommits;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getCommitMessageRegex() {
    return commitMessageRegex;
  }

  public void setCommitMessageRegex(String commitMessageRegex) {
    this.commitMessageRegex = commitMessageRegex;
  }

  public String getCommitMessageNegativeRegex() {
    return commitMessageNegativeRegex;
  }

  public void setCommitMessageNegativeRegex(String commitMessageNegativeRegex) {
    this.commitMessageNegativeRegex = commitMessageNegativeRegex;
  }

  public String getBranchNameRegex() {
    return branchNameRegex;
  }

  public void setBranchNameRegex(String branchNameRegex) {
    this.branchNameRegex = branchNameRegex;
  }

  public Boolean getDenyDeleteTag() {
    return denyDeleteTag;
  }

  public void setDenyDeleteTag(Boolean denyDeleteTag) {
    this.denyDeleteTag = denyDeleteTag;
  }

  public Boolean getMemberCheck() {
    return memberCheck;
  }

  public void setMemberCheck(Boolean memberCheck) {
    this.memberCheck = memberCheck;
  }

  public Boolean getPreventSecrets() {
    return preventSecrets;
  }

  public void setPreventSecrets(Boolean preventSecrets) {
    this.preventSecrets = preventSecrets;
  }

  public String getAuthorEmailRegex() {
    return authorEmailRegex;
  }

  public void setAuthorEmailRegex(String authorEmailRegex) {
    this.authorEmailRegex = authorEmailRegex;
  }

  public String getFileNameRegex() {
    return fileNameRegex;
  }

  public void setFileNameRegex(String fileNameRegex) {
    this.fileNameRegex = fileNameRegex;
  }

  public Integer getMaxFileSize() {
    return maxFileSize;
  }

  public void setMaxFileSize(Integer maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  public Boolean getCommitCommitterCheck() {
    return commitCommitterCheck;
  }

  public void setCommitCommitterCheck(Boolean commitCommitterCheck) {
    this.commitCommitterCheck = commitCommitterCheck;
  }

  public Boolean getRejectUnsignedCommits() {
    return rejectUnsignedCommits;
  }

  public void setRejectUnsignedCommits(Boolean rejectUnsignedCommits) {
    this.rejectUnsignedCommits = rejectUnsignedCommits;
  }

  public boolean isRuleActive(String ruleName, String value) {
    switch (ruleName) {
    case "commit_message_regex" :
      return commitMessageRegex.equals(value);
    case "commit_message_nagative_regex" :
      return commitMessageNegativeRegex.equals(value);
    case "branch_name_regex" :
      return branchNameRegex.equals(value);
    case "denyDeleteTag" :
      return Boolean.toString(denyDeleteTag).equals(value);
    case "member_check" :
      return Boolean.toString(memberCheck).equals(value);
    case "prevent_secrets" :
      return Boolean.toString(preventSecrets).equals(value);
    case "author_email_regex" :
      return authorEmailRegex.equals(value);
    case "file_name_regex" :
      return fileNameRegex.equals(value);
    case "max_file_size" :
      return Integer.toString(maxFileSize).equals(value);
    case "commit_committer_check" :
      return Boolean.toString(commitCommitterCheck).equals(value);
    case "reject_unsigned_commits" :
      return Boolean.toString(rejectUnsignedCommits).equals(value);
    default:
      throw new IllegalStateException("Unknown rule name '" + ruleName + "'");
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PushRules other = (PushRules) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "PushRules [id=" + id + ", createdAt=" + createdAt + ", commitMessageRegex=" + commitMessageRegex
        + ", commitMessageNegativeRegex=" + commitMessageNegativeRegex + ", branchNameRegex=" + branchNameRegex
        + ", denyDeleteTag=" + denyDeleteTag + ", memberCheck=" + memberCheck + ", preventSecrets=" + preventSecrets
        + ", authorEmailRegex=" + authorEmailRegex + ", fileNameRegex=" + fileNameRegex + ", maxFileSize="
        + maxFileSize + ", commitCommitterCheck=" + commitCommitterCheck + ", rejectUnsignedCommits="
        + rejectUnsignedCommits + "]";
  }
}
