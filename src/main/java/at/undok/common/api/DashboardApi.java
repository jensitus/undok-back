package at.undok.common.api;

import at.undok.common.dto.TimelineItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/dashboard")
public interface DashboardApi {

    @GetMapping("/timeline/items")
    List<TimelineItemDto> getItemsByUserId();

    @GetMapping("/timeline/{user_id}/items/month")
    ResponseEntity getItemsByUserIdSortedByMonth(@PathVariable("user_id") Long user_id);

    @GetMapping("/chart/country-chart-data")
    Object[] getCountryChartData();

}
