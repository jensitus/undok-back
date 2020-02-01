package org.service.b.common.repository;

import org.service.b.common.model.CleaningUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleaningUpRepo extends JpaRepository<CleaningUp, String> {

  CleaningUp findByProcessDefinitionKey(String processDefinitionKey);

}
