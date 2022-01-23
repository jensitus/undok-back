package at.undok.common.service;

import at.undok.undok.client.model.dto.AllClientDto;
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
        List<AllClientDto> all = clientService.getAll();

        List<String> nationalities = new ArrayList<>();
        for (AllClientDto clientDto : all) {
            if (clientDto.getNationality() == null) {
                nationalities.add("unknown");
            } else {
                nationalities.add(clientDto.getNationality());
            }
        }

        List<String> nationalitiesSorted = new ArrayList<>();
        for (String nationality : nationalities) {
            if (!nationalitiesSorted.contains(nationality)) {
                nationalitiesSorted.add(nationality);
            }
        }
        List<Integer> nationalityNumbers = new ArrayList<>();
        for (String cName : nationalitiesSorted) {
            nationalityNumbers.add(countCountryNames(cName, nationalities));
        }

        Object[] nationalityDataChartArray =  new Object[2];
        String[] cSorted = nationalitiesSorted.toArray(new String[0]);

        int[] numbers = new int[nationalityNumbers.size()];
        for (int i = 0; i < nationalityNumbers.size(); i++) {
            numbers[i] = nationalityNumbers.get(i);
        }


        nationalityDataChartArray[0] = cSorted;
        nationalityDataChartArray[1] = numbers;

        return nationalityDataChartArray;
    }

    private int countCountryNames(String nationality, List<String> nationalities) {
        int nationalityCounter = 0;
        for (String cName : nationalities) {
            if (cName == null && nationality == null) {
                nationalityCounter = nationalityCounter + 1;
            } else if (cName != null && nationality != null) {
                if (cName.equals(nationality)) {
                    nationalityCounter = nationalityCounter + 1;
                }
            } else {
                // what should we do
            }
        }
        return nationalityCounter;
    }

}
