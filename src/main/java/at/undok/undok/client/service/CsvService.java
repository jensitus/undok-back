package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.AllCounselingDto;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvService {

    private final ClientService clientService;
    private final CounselingService counselingService;

    private static final String[] HEADERS = {"firstName", "lastName", "keyword"};


    @SneakyThrows
    public void writeClientsToCsv() {
        FileWriter fileOut = new FileWriter("csv/clients.csv");
        try (CSVPrinter csvPrinter = new CSVPrinter(fileOut, CSVFormat.DEFAULT.withHeader(HEADERS))) {
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

    public static ByteArrayInputStream counselingsToCSV(List<AllCounselingDto> counselingDtos) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (AllCounselingDto counselingDto : counselingDtos) {
                List<String> data = Arrays.asList(
                        String.valueOf(counselingDto.getId()),
                        counselingDto.getKeyword(),
                        counselingDto.getActivity()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    public void writeCounselingsToCsv() {

    }

}
