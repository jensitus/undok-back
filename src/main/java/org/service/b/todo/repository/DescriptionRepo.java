package org.service.b.todo.repository;

import org.service.b.todo.dto.DescriptionDto;
import org.service.b.todo.model.Description;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DescriptionRepo extends JpaRepository<Description, Long> {

  List findByItemIdOrderByCreatedAt(Long itemId);

}
