package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.util.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvService {

    private final ClientService clientService;
    private final CounselingService counselingService;
    private final CategoryService categoryService;

    private static final String[] CLIENT_HEADERS = {"firstName", "lastName", "keyword"};

    public void writeClientsToCsv() throws IOException {
        FileWriter fileOut = new FileWriter("csv/clients.csv");
        try (CSVPrinter csvPrinter = new CSVPrinter(fileOut, CSVFormat.DEFAULT.withHeader(CLIENT_HEADERS))) {
            List<AllClientDto> all = clientService.getAll();
            for (AllClientDto clientDto : all) {
                csvPrinter.printRecord(clientDto.getFirstName(), clientDto.getLastName(), clientDto.getKeyword());
            }
        } catch (IOException e) {
            log.error("error while writing csv {}", e.toString());
        }
    }

    public ByteArrayInputStream load() {
        List<AllCounselingDto> allCounselings = counselingService.getAllCounselings();
        return counselingsToCSV(allCounselings);
    }

    private static final String[] COUNSELING_HEADERS = {"id", "Keyword", "concern", "Legal Categories", "activity",
            "activityCategories", "registeredBy", "counselingDate", "clientFullName", "comment"};

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
