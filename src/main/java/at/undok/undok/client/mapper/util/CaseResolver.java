package at.undok.undok.client.mapper.util;

import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.repository.CaseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CaseResolver {

    private final CaseRepo caseRepo;

    public Case resolve(UUID caseId) {
        return caseId != null ? caseRepo.findById(caseId).orElse(null) : null;
    }

}
