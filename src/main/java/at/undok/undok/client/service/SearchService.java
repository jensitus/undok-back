package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.FullyDto;
import at.undok.undok.client.repository.FullyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private FullyRepo fullyRepo;
    private final AttributeEncryptor attributeEncryptor;

    public List<FullyDto> searchFully(String searchTerm) {
        String encryptedSearchTerm = attributeEncryptor.convertToDatabaseColumn(searchTerm);
        return null;
    }

}
