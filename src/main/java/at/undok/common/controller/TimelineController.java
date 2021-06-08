package at.undok.common.controller;

import at.undok.common.dto.TimelineItemDto;
import at.undok.common.message.service.TimelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RestController
@RequestMapping("/timeline")
public class TimelineController {
  private static final Logger logger = LoggerFactory.getLogger(TimelineController.class);

  @Autowired
  private TimelineService timelineService;

  @GetMapping("/{user_id}/items")
  public ResponseEntity getItemsByUserId(@PathVariable("user_id") Long user_id) {
    List<TimelineItemDto> itemsForTimeline = timelineService.getItemsForTimeline(user_id);
    List itemList = itemsForTimeline;
    return new ResponseEntity(itemList, HttpStatus.OK);
  }

  @GetMapping("/{user_id}/items/month")
  public ResponseEntity getItemsByUserIdSortedByMonth(@PathVariable("user_id") Long user_id) {
    return new ResponseEntity(timelineService.getItemsGroupedByMonth(user_id), HttpStatus.OK);
  }

}
