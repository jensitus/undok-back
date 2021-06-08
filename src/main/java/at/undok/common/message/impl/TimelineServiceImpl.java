package at.undok.common.message.impl;

import org.modelmapper.ModelMapper;
import at.undok.common.dto.TimelineItemDto;
import at.undok.common.message.service.TimelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimelineServiceImpl implements TimelineService {

  private static final Logger logger = LoggerFactory.getLogger(TimelineServiceImpl.class);

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public List<TimelineItemDto> getItemsForTimeline(Long user_id) {
    List<TimelineItemDto> timelineItemDtoList = getTimelineDtos(user_id);
    List<TimelineItemDto> dueDateFirstList = timelineItemDtoList.stream().filter(x -> x.getDueDate() != null).collect(Collectors.toList());
    for (TimelineItemDto t : timelineItemDtoList) {
      if (t.getDueDate() == null) {
        dueDateFirstList.add(t);
      }
    }
    return dueDateFirstList;
  }

  @Override
  public Map<String, ?> getItemsGroupedByMonth(Long user_id) {
//    List<TimelineItemDto> timelineItemDtos = getTimelineDtos(user_id);
//    Map timelineItemMap = timelineItemDtos.stream().collect(Collectors.groupingBy(i -> i.getCreatedAt().getMonth()));
//    for (TimelineItemDto dto : timelineItemDtos) {
//      dto.getCreatedAt().getMonth();
//    }
    Map timelineItemMap = new HashMap();
    return timelineItemMap;

  }

//  private TodoDto getTodoDto(Long id) {
//    Todo todo = todoRepo.getOne(id);
//    return modelMapper.map(todo, TodoDto.class);
//  }

  private List<TimelineItemDto> getTimelineDtos(Long user_id) {
    List<TimelineItemDto> timelineItemDtos = new ArrayList<>();
//    List<Item> oldItems = itemRepo.findItemsByUserIdOrderByCreatedAt(user_id);
//    // oldItems.stream().filter(oldItem -> oldItemDtos.add(modelMapper.map(oldItem, ItemDto.class)));
//    for (Item item : oldItems) {
//      TodoDto todoDto = getTodoDto(item.getTodoId());
//      TimelineItemDto timelineItemDto = modelMapper.map(item, TimelineItemDto.class);
//      String taskId = serviceBTaskService.getTaskForTimeLine(timelineItemDto.getTodoId());
//      timelineItemDto.setTaskId(taskId);
//      timelineItemDto.setTodoTitle(todoDto.getTitle());
//      timelineItemDtos.add(timelineItemDto);
//    }
    return timelineItemDtos;
  }



}
