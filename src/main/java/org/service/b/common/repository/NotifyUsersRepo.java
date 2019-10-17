package org.service.b.common.repository;

import org.service.b.common.model.ModelType;
import org.service.b.common.model.NotifyUsers;
import org.service.b.common.model.WhatIsReported;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotifyUsersRepo extends JpaRepository<NotifyUsers, String> {

  NotifyUsers findById_(String id_);

  List findByModelTypeAndNotified(ModelType modelType, boolean notified);

  List findByWhatIsReportedAndNotified(WhatIsReported whatIsReported, boolean notified);

  NotifyUsers findByStringId(String string_id);

}
