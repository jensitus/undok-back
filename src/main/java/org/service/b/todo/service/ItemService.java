package org.service.b.todo.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.common.dto.TimelineItemDto;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.model.Item;

import java.time.LocalDate;
import java.util.List;

public interface ItemService {

  ItemDto createItem(Long todo_id, String name);

  ItemDto updateItem(Long item_id);

  void deleteItem(Long item_id);

  ItemDto setItemDueDate(Long item_id, String dueDate);

  List<TimelineItemDto> getItemsForTimeline(Long user_id);

}
