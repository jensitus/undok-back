package org.service.b.common;

public class MigrationForm {

  private String processInstanceId;
  private String sourceVersion;
  private String targetVersion;
  private String sourceAct;
  private String targetAct;

  public MigrationForm() {
  }

  public MigrationForm(String processInstanceId, String sourceVersion, String targetVersion, String sourceAct, String targetAct) {
    this.processInstanceId = processInstanceId;
    this.sourceVersion = sourceVersion;
    this.targetVersion = targetVersion;
    this.sourceAct = sourceAct;
    this.targetAct = targetAct;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getSourceVersion() {
    return sourceVersion;
  }

  public void setSourceVersion(String sourceVersion) {
    this.sourceVersion = sourceVersion;
  }

  public String getTargetVersion() {
    return targetVersion;
  }

  public void setTargetVersion(String targetVersion) {
    this.targetVersion = targetVersion;
  }

  public String getSourceAct() {
    return sourceAct;
  }

  public void setSourceAct(String sourceAct) {
    this.sourceAct = sourceAct;
  }

  public String getTargetAct() {
    return targetAct;
  }

  public void setTargetAct(String targetAct) {
    this.targetAct = targetAct;
  }

  @Override
  public String toString() {
    return "MigrationForm{" +
            "processInstanceId='" + processInstanceId + '\'' +
            ", sourceVersion='" + sourceVersion + '\'' +
            ", targetVersion='" + targetVersion + '\'' +
            ", sourceAct='" + sourceAct + '\'' +
            ", targetAct='" + targetAct + '\'' +
            '}';
  }
}
