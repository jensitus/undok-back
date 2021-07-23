package at.undok.common.controller;

import at.undok.common.dto.TimelineItemDto;
import at.undok.common.message.impl.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RestController
@RequestMapping("/timeline")
@RequiredArgsConstructor
public class TimelineController {

  private final TimelineService timelineService;

  @GetMapping("/items")
  public List<TimelineItemDto> getItemsByUserId() {
    return timelineService.getItemsForTimeline();
  }

  @GetMapping("/{user_id}/items/month")
  public ResponseEntity getItemsByUserIdSortedByMonth(@PathVariable("user_id") Long user_id) {
    return new ResponseEntity(timelineService.getItemsGroupedByMonth(user_id), HttpStatus.OK);
  }

}
