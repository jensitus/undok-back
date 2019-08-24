package org.service.b.common.dto;

import org.camunda.bpm.engine.task.DelegationState;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDto {

  private String id;

  private String owner;

  private String assignee;

  private String name;

  private String description;

  private int priority;

  private DelegationState delegationState;

  private String processInstanceId;

  private String executionId;

  private String processDefinitionId;

  private String caseInstanceId;

  private String caseExecutionId;

  private String caseDefinitionId;

  private LocalDateTime createTime;

  private String taskDefinitionKey;

  private LocalDate dueDate;

  private LocalDate followUpDate;

  private String parentTaskId;

  private boolean suspended;

  private String formKey;

  private String tenantId;

  public TaskDto() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public DelegationState getDelegationState() {
    return delegationState;
  }

  public void setDelegationState(DelegationState delegationState) {
    this.delegationState = delegationState;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  public String getCaseInstanceId() {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId) {
    this.caseInstanceId = caseInstanceId;
  }

  public String getCaseExecutionId() {
    return caseExecutionId;
  }

  public void setCaseExecutionId(String caseExecutionId) {
    this.caseExecutionId = caseExecutionId;
  }

  public String getCaseDefinitionId() {
    return caseDefinitionId;
  }

  public void setCaseDefinitionId(String caseDefinitionId) {
    this.caseDefinitionId = caseDefinitionId;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }

  public String getTaskDefinitionKey() {
    return taskDefinitionKey;
  }

  public void setTaskDefinitionKey(String taskDefinitionKey) {
    this.taskDefinitionKey = taskDefinitionKey;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDate getFollowUpDate() {
    return followUpDate;
  }

  public void setFollowUpDate(LocalDate followUpDate) {
    this.followUpDate = followUpDate;
  }

  public String getParentTaskId() {
    return parentTaskId;
  }

  public void setParentTaskId(String parentTaskId) {
    this.parentTaskId = parentTaskId;
  }

  public boolean isSuspended() {
    return suspended;
  }

  public void setSuspended(boolean suspended) {
    this.suspended = suspended;
  }

  public String getFormKey() {
    return formKey;
  }

  public void setFormKey(String formKey) {
    this.formKey = formKey;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  @Override
  public String toString() {
    return "TaskDto{" +
            "id='" + id + '\'' +
            ", owner='" + owner + '\'' +
            ", assignee='" + assignee + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", priority=" + priority +
            ", delegationState=" + delegationState +
            ", processInstanceId='" + processInstanceId + '\'' +
            ", executionId='" + executionId + '\'' +
            ", processDefinitionId='" + processDefinitionId + '\'' +
            ", caseInstanceId='" + caseInstanceId + '\'' +
            ", caseExecutionId='" + caseExecutionId + '\'' +
            ", caseDefinitionId='" + caseDefinitionId + '\'' +
            ", createTime=" + createTime +
            ", taskDefinitionKey='" + taskDefinitionKey + '\'' +
            ", dueDate=" + dueDate +
            ", followUpDate=" + followUpDate +
            ", parentTaskId='" + parentTaskId + '\'' +
            ", suspended=" + suspended +
            ", formKey='" + formKey + '\'' +
            ", tenantId='" + tenantId + '\'' +
            '}';
  }
}
