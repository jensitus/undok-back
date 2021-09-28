package at.undok.common.service;

import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChartService {

    private final ClientService clientService;

    public ChartService(ClientService clientService) {
        this.clientService = clientService;
    }

    public Object[] getCountryChartData() {
        List<ClientDto> all = clientService.getAll();

        List<String> countryNames = new ArrayList<>();
        for (ClientDto clientDto : all) {
            if (clientDto.getPerson().getAddress().getCountry() == null) {
                countryNames.add("unknown");
            } else {
                countryNames.add(clientDto.getPerson().getAddress().getCountry());
            }
        }

        List<String> countryNamesSorted = new ArrayList<>();
        for (String countryName : countryNames) {
            if (!countryNamesSorted.contains(countryName)) {
                countryNamesSorted.add(countryName);
            }
        }
        List<Integer> countryNumbers = new ArrayList<>();
        for (String cName : countryNamesSorted) {
            countryNumbers.add(countCountryNames(cName, countryNames));
        }

        log.info(countryNamesSorted.toString());
        log.info(countryNumbers.toString());

        Object[] countryDataChartArray =  new Object[2];
        String[] cSorted = countryNamesSorted.toArray(new String[0]);

        int[] numbers = new int[countryNumbers.size()];
        for (int i = 0; i < countryNumbers.size(); i++) {
            numbers[i] = countryNumbers.get(i);
        }


        countryDataChartArray[0] = cSorted;
        countryDataChartArray[1] = numbers;

        return countryDataChartArray;
    }

    private int countCountryNames(String countryName, List<String> countryNames) {
        int countryCounter = 0;
        for (String cName : countryNames) {
            if (cName == null && countryName == null) {
                countryCounter = countryCounter + 1;
            } else if (cName != null && countryName != null) {
                if (cName.equals(countryName)) {
                    countryCounter = countryCounter + 1;
                }
            } else {
                // what should we do
            }
        }
        return countryCounter;
    }

}
