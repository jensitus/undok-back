package org.service.b.common.message.service;

import org.service.b.common.dto.TimelineItemDto;

import java.util.List;
import java.util.Map;

public interface TimelineService {

  List<TimelineItemDto> getItemsForTimeline(Long user_id);

  Map<String, ?> getItemsGroupedByMonth(Long user_id);

}
