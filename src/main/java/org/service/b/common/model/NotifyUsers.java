package org.service.b.common.model;

import javax.persistence.*;

@Entity
@Table(name = "notify_users")
public class NotifyUsers {

  @Column(name = "id")
  private String id;

  @Id
  @Column(name = "string_id")
  private String stringId;

  @Column(name = "id_")
  private String id_;

  @Column(name = "notified")
  private boolean notified;

  @Column(name = "model_id")
  private Long modelId;

  @Enumerated(EnumType.STRING)
  @Column(name = "model_type")
  private ModelType modelType;

  @Enumerated(EnumType.STRING)
  @Column(name = "what_is_reported")
  private WhatIsReported whatIsReported;

  public NotifyUsers() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStringId() {
    return stringId;
  }

  public void setStringId(String stringId) {
    this.stringId = stringId;
  }

  public String getId_() {
    return id_;
  }

  public void setId_(String id_) {
    this.id_ = id_;
  }

  public boolean isNotified() {
    return notified;
  }

  public void setNotified(boolean notified) {
    this.notified = notified;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public ModelType getModelType() {
    return modelType;
  }

  public void setModelType(ModelType modelType) {
    this.modelType = modelType;
  }

  public WhatIsReported getWhatIsReported() {
    return whatIsReported;
  }

  public void setWhatIsReported(WhatIsReported whatIsReported) {
    this.whatIsReported = whatIsReported;
  }

  @Override
  public String toString() {
    return "NotifyUsers{" +
            "id='" + id + '\'' +
            ", stringId='" + stringId + '\'' +
            ", id_='" + id_ + '\'' +
            ", notified=" + notified +
            ", modelId=" + modelId +
            ", modelType=" + modelType +
            ", whatIsReported=" + whatIsReported +
            '}';
  }
}
