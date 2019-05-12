package org.service.b.todo.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.model.Item;

public interface ItemService {

  Item createItem(Long todo_id, String name);

  ItemDto updateItem(Long item_id);

  void deleteItem(Long item_id);

}
