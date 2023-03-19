package at.undok.undok.client.service;

import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.util.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final ClientService clientService;
    private final CounselingService counselingService;
    private final CategoryService categoryService;
    private final ToLocalDateService localDateService;

    private static final String[] COUNSELING_HEADERS = {"id", "Keyword", "concern", "Legal Categories", "activity",
            "activityCategories", "registeredBy", "counselingDate", "clientFullName", "comment"};


    public ByteArrayInputStream loadClientCsv() {
        List<AllClientDto> clientDtos = clientService.getAll();
        return writeClientsToCsv(clientDtos);
    }

    private static final String[] CLIENT_HEADERS = {"id", "keyword", "firstName", "lastName", "dateOfBirth", "email",
            "telephone", "street", "zipCode", "city", "country", "education", "maritalStatus", "interpreterNecessary",
            "howHasThePersonHeardFromUs", "vulnerableWhenAssertingRights", "nationality", "language",
            "currentResidentStatus", "labourMarketAccess", "position", "sector", "union",
            "membership", "organization", "gender"};

    private ByteArrayInputStream writeClientsToCsv(List<AllClientDto> clientDtos) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL).withHeader(CLIENT_HEADERS);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (AllClientDto clientDto : clientDtos) {
                String interpreterNecessary = null;
                if (clientDto.getInterpreterNecessary() != null) {
                     interpreterNecessary = clientDto.getInterpreterNecessary().toString();
                }
                String getVulnerableWhenAssertingRights = null;
                if (clientDto.getVulnerableWhenAssertingRights() != null) {
                    getVulnerableWhenAssertingRights = clientDto.getVulnerableWhenAssertingRights().toString();
                }
                String getMembership = null;
                if (clientDto.getMembership() != null) {
                    getMembership = clientDto.getMembership().toString();
                }
                String dateOfBirth = null;
                if (clientDto.getDateOfBirth() != null) {
                    dateOfBirth = localDateService.localDateToString(clientDto.getDateOfBirth());
                }
                List<String> data = Arrays.asList(
                        String.valueOf(clientDto.getId()),
                        clientDto.getKeyword(),
                        clientDto.getFirstName(),
                        clientDto.getLastName(),
                        dateOfBirth,
                        clientDto.getEmail(),
                        clientDto.getTelephone(),
                        clientDto.getStreet(),
                        clientDto.getZipCode(),
                        clientDto.getCity(),
                        clientDto.getCountry(),
                        clientDto.getEducation(),
                        clientDto.getMaritalStatus(),
                        interpreterNecessary,
                        clientDto.getHowHasThePersonHeardFromUs(),
                        getVulnerableWhenAssertingRights,
                        clientDto.getNationality(),
                        clientDto.getLanguage(),
                        clientDto.getCurrentResidentStatus(),
                        clientDto.getLabourMarketAccess(),
                        clientDto.getPosition(),
                        clientDto.getSector(),
                        clientDto.getUnion(),
                        getMembership,
                        clientDto.getOrganization(),
                        clientDto.getGender()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("error while writing csv {}", e);
        }
    }

    public ByteArrayInputStream load() {
        List<AllCounselingDto> allCounselings = counselingService.getAllCounselings();
        return counselingsToCSV(allCounselings);
    }

    public ByteArrayInputStream counselingsToCSV(List<AllCounselingDto> counselingDtos) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL).withHeader(COUNSELING_HEADERS);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (AllCounselingDto counselingDto : counselingDtos) {
                List<String> data = Arrays.asList(
                        String.valueOf(counselingDto.getId()),
                        counselingDto.getKeyword(),
                        counselingDto.getConcern(),
                        getCategories(CategoryType.LEGAL, counselingDto.getId()),
                        counselingDto.getActivity(),
                        getCategories(CategoryType.ACTIVITY, counselingDto.getId()),
                        counselingDto.getRegisteredBy(),
                        counselingDto.getCounselingDate().toString(),
                        counselingDto.getClientFullName(),
                        counselingDto.getComment()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    private String getCategories(String categoryType, UUID entityId) {
        List<CategoryDto> categoryListByTypeAndEntity = categoryService.getCategoryListByTypeAndEntity(categoryType, entityId);
        StringJoiner sj = new StringJoiner(",");
        for (CategoryDto categoryDto : categoryListByTypeAndEntity) {
            sj.add(categoryDto.getName());
        }
        return sj.toString();
    }

}
