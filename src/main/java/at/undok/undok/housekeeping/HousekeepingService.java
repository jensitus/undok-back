package at.undok.undok.housekeeping;

import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.service.CounselingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class HousekeepingService {

    private final CounselingService counselingService;

    @Scheduled(cron = "${housekeeping.schedule}")
    public void cleanUpCounselings() {
        log.info("scheduled counseling clean up");
        List<Counseling> counselings = counselingService.getCounselingsWithoutDate();
        if (counselings.size() > 0) {
            delete(counselings);
        }
    }

    private void delete(List<Counseling> counselings) {
        log.info(counselings.size() + " counselings without proper date to be deleted");
        for (Counseling c : counselings) {
            counselingService.deleteCounseling(c.getId());
        }
    }

}
