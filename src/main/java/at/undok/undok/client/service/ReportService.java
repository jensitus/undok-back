package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CounselingRepo counselingRepo;
    private final ClientRepo clientRepo;

    public Long numberOfCounselingsByDateRange(LocalDateTime from, LocalDateTime to) {
        validate(from, to);
        return counselingRepo.countByCounselingDateGreaterThanEqualAndCounselingDateLessThan(from, to);
    }

    public Long numberOfClientsWithFirstCounselingInDateRange(LocalDateTime from, LocalDateTime to) {
        validate(from, to);
        return clientRepo.countClientsWithFirstCounselingInRange(from, to);
    }

    public List<LanguageCount> countByLanguageInDateRange(LocalDateTime from, LocalDateTime to) {
        validate(from, to);
        return clientRepo.countByLanguageInDateRange(from, to).stream().map(LanguageCount::from).collect(Collectors.toList());
    }

    public List<NationalityCount> getNationalityCountsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        validate(fromDate, toDate);
        return clientRepo.countByNationalityInDateRange(fromDate, toDate).stream().map(NationalityCount::from).collect(Collectors.toList());
    }

    public List<GenderCount> getGenderCountsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        validate(fromDate, toDate);
        return clientRepo.countByGenderInDateRange(fromDate, toDate).stream().map(GenderCount::from).collect(Collectors.toList());
    }

    public List<SectorCount> getSectorCountsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        validate(fromDate, toDate);
        return clientRepo.countBySectorInDateRange(fromDate, toDate).stream().map(SectorCount::from).collect(Collectors.toList());
    }

    public List<CounselingActivityCount> getCounselingActivityCountsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        validate(fromDate, toDate);
        return clientRepo.countByActivityInDateRange(fromDate, toDate).stream().map(CounselingActivityCount::from).collect(Collectors.toList());
    }

    private void validate(LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("from and to must be provided");
        }
        if (fromDate.isAfter(toDate) || fromDate.isEqual(toDate)) {
            throw new IllegalArgumentException("'from' date must be before 'to' date");
        }
    }

}
