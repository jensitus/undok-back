package at.undok.common.controller;

import at.undok.common.api.DashboardApi;
import at.undok.common.dto.TimelineItemDto;
import at.undok.common.message.impl.TimelineService;
import at.undok.common.service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

  private final TimelineService timelineService;
  private final ChartService chartService;

  @Override
  public List<TimelineItemDto> getItemsByUserId() {
    return timelineService.getItemsForTimeline();
  }

  @Override
  public ResponseEntity getItemsByUserIdSortedByMonth(Long user_id) {
    return new ResponseEntity(timelineService.getItemsGroupedByMonth(user_id), HttpStatus.OK);
  }

  @Override
  public Object[] getCountryChartData() {
    return chartService.getCountryChartData();
  }

}
