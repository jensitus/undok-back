package org.service.b.todo.repository;

import org.service.b.todo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepo extends JpaRepository<Item, Long> {

  List findByTodoIdOrderByCreatedAt(Long todo_id);

}
