package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.ClientDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class CsvService {

    private final ClientService clientService;

    private static final String[] HEADERS = {"firstName", "lastName", "keyword"};

    public CsvService(ClientService clientService) {
        this.clientService = clientService;
    }

    public void writeClientsToCsv() throws IOException {
        FileWriter fileOut = new FileWriter("csv/clients.csv");
        try (CSVPrinter csvPrinter = new CSVPrinter(fileOut, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            List<ClientDto> all = clientService.getAll();
            for (ClientDto clientDto : all) {
                csvPrinter.printRecord(clientDto.getPerson().getFirstName(), clientDto.getPerson().getLastName(), clientDto.getKeyword());
            }
        } catch (IOException e) {
            log.error("error while writing csv {}", e.toString());
        }
    }

}
