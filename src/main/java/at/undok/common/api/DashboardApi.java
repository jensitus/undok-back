package at.undok.common.api;

import at.undok.common.dto.TimelineItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RequestMapping("/dashboard")
public interface DashboardApi {

    @GetMapping("/timeline/items")
    public List<TimelineItemDto> getItemsByUserId();

    @GetMapping("/timeline/{user_id}/items/month")
    public ResponseEntity getItemsByUserIdSortedByMonth(@PathVariable("user_id") Long user_id);

    @GetMapping("/chart/country-chart-data")
    Object[] getCountryChartData();

}
