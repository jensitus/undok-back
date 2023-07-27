package at.undok.undok.client.service;

import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.exception.CsvNotFoundException;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.CounselingResult;
import at.undok.undok.client.util.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class CsvService {

    private final ClientService clientService;
    private final CounselingService counselingService;
    private final CategoryService categoryService;
    private final ToLocalDateService localDateService;

    private static final String[] COUNSELING_HEADERS = {"id", "Keyword", "Anliegen", "Rechtskategorien", "Aktivität",
            "Aktivitätskategorien", "Registriert von", "Beratungsdatum", "Clientname", "Kommentar"};


    public ByteArrayInputStream loadClientCsv() {
        List<AllClientDto> clientDtos = clientService.getAll();
        return writeClientsToCsv(clientDtos);
    }

    private static final String[] CLIENT_HEADERS = {"id", "keyword", "Vorname", "Nachname", "Geburtsdatum", "Email",
            "Telephon", "Straße", "Plz", "Stadt", "Land", "Bildung", "Familienstatus", "Dolmetsch erforderlich",
            "Woher kennt uns die Person", "gefährdet bei Geltendmachung", "Nationalität", "Sprache",
            "Aufenthaltsstatus", "Arbeitsmarktzugang", "Position", "Branche", "Gewerkschaft",
            "Mitgliedschaft", "Organization", "Gender"};

    private static final String CSV_DIR = "csv/";

    private ByteArrayInputStream writeClientsToCsv(List<AllClientDto> clientDtos) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL).withHeader(CLIENT_HEADERS);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (AllClientDto clientDto : clientDtos) {
                List<String> data = iterateThroughClients(clientDto);
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("error while writing csv {}", e);
        }
    }

    private List<String> iterateThroughClients(AllClientDto clientDto) {
        List<String> data;

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
        data = Arrays.asList(
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
        return data;
    }

    public ByteArrayInputStream load() {
        List<AllCounselingDto> allCounselings = counselingService.getAllCounselings();
        return counselingsToCSV(allCounselings);
    }

    public Set<String> getCsvFileNames() {
        return getCsvFileNamesFromDirectory();
    }

    public ByteArrayInputStream counselingsToCSV(List<AllCounselingDto> counselingDtos) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL).withHeader(COUNSELING_HEADERS);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            List<String> data = counselingDataForCsv(counselingDtos);
            csvPrinter.printRecord(data);
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    private List<String> counselingDataForCsv(List<AllCounselingDto> counselingDtos) {
        List<String> data = null;
        for (AllCounselingDto counselingDto : counselingDtos) {
             data = Arrays.asList(
                    String.valueOf(counselingDto.getId()),
                    counselingDto.getKeyword(),
                    counselingDto.getConcern(),
                    getCategories(CategoryType.LEGAL, counselingDto.getId()),
                    counselingDto.getActivity(),
                    getCategories(CategoryType.ACTIVITY, counselingDto.getId()),
                    counselingDto.getRegisteredBy(),
                    localDateService.localDateTimeToString(counselingDto.getCounselingDate()),
                    counselingDto.getClientFullName(),
                    counselingDto.getComment()
            );
        }
        return data;
    }

    private List<String> counselingsForCsv(CounselingResult counselingResult) {
        List<String> data = null;
            data = Arrays.asList(
                    String.valueOf(counselingResult.getId()),
                    counselingResult.getKeyword(),
                    counselingResult.getConcern(),
                    // getCategories(CategoryType.LEGAL, counselingResult.getId()),
                    counselingResult.getLegalCategories(),
                    counselingResult.getActivity(),
                    // getCategories(CategoryType.ACTIVITY, counselingResult.getId()),
                    counselingResult.getActivityCategories(),
                    counselingResult.getRegisteredBy(),
                    localDateService.localDateTimeToString(counselingResult.getCounselingDate()),
                    // counselingResult.getClientFullName(),
                    counselingResult.getComment()
            );
        return data;
    }

    private String getCategories(String categoryType, UUID entityId) {
        List<CategoryDto> categoryListByTypeAndEntity = categoryService.getCategoryListByTypeAndEntity(categoryType, entityId);
        StringJoiner sj = new StringJoiner(",");
        for (CategoryDto categoryDto : categoryListByTypeAndEntity) {
            sj.add(categoryDto.getName());
        }
        return sj.toString();
    }

    @Scheduled(cron = "${csv-backup.schedule}")
    public void csvAsBackup() {
        log.info("- - - - - start csv backup - - - - -");
        clientsBackup();
        counselingsBackup();
        deleteOldCSVs();
    }

    @SneakyThrows
    private void clientsBackup() {
        String fileName = LocalDateTime.now() + "-clients.csv";
        FileWriter clientFileOut = new FileWriter(CSV_DIR + fileName);
        try (CSVPrinter csvPrinter = new CSVPrinter(clientFileOut, CSVFormat.DEFAULT.withHeader(CLIENT_HEADERS))) {
            List<AllClientDto> all = clientService.getAll();
            for (AllClientDto clientDto : all) {
                List<String> data = iterateThroughClients(clientDto);
                csvPrinter.printRecord(data);
            }
        } catch (IOException e) {
            log.error("error while writing csv {}", e.toString());
        }
    }

    @SneakyThrows
    private void counselingsBackup() {
        String fileName = LocalDateTime.now() + "-counselings.csv";
        FileWriter counselingFileOut = new FileWriter(CSV_DIR + fileName);
        try (CSVPrinter csvPrinter = new CSVPrinter(counselingFileOut, CSVFormat.DEFAULT.withHeader(COUNSELING_HEADERS))) {
            List<CounselingResult> counselingResults = counselingService.getCounselingsForCsv();
            for (CounselingResult counselingResult : counselingResults) {
                List<String> data = counselingsForCsv(counselingResult);
                csvPrinter.printRecord(data);
            }
        } catch (IOException e) {
            log.error("error while writing csv {}", e.toString());
        }
    }

    @SneakyThrows
    private void deleteOldCSVs() {
        Set<String> stringSet = getCsvFileNamesFromDirectory();
        for (String fileName : stringSet) {
            String[] split = fileName.split("T");
            LocalDate localDateTime = LocalDate.parse(split[0]);
            if (localDateTime.isBefore(LocalDate.now().minusDays(7))) {
                Path toDelete = Paths.get(CSV_DIR + fileName);
                log.info("CSV {} to delete " + toDelete.toString());
                Files.delete(toDelete);
            }
        }
    }

    private Set<String> getCsvFileNamesFromDirectory() {
        try {
            return Stream.of(Objects.requireNonNull(new File(CSV_DIR).listFiles()))
                         .filter(file -> !file.isDirectory()).map(File::getName).collect(Collectors.toSet());
        } catch (NullPointerException e) {
            throw new CsvNotFoundException(HttpStatus.INSUFFICIENT_STORAGE, "sorry, we didn't find any CSV File ");
        }

    }

    @SneakyThrows
    public ByteArrayResource getBackupCsv(String fileName) {
        Path backupPath = Paths.get(CSV_DIR + fileName);
        return new ByteArrayResource(Files.readAllBytes(backupPath));
    }

}
