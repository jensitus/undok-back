package at.undok.common.message.service;

import at.undok.common.dto.TimelineItemDto;

import java.util.List;
import java.util.Map;

public interface TimelineServiceOldInter {

  List<TimelineItemDto> getItemsForTimeline(Long user_id);

  Map<String, ?> getItemsGroupedByMonth(Long user_id);

}
