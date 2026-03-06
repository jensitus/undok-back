package at.undok.undok.client.service;

import at.undok.undok.client.mapper.impl.ClientMapperImpl;
import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.mapper.inter.ClientMapper;
import at.undok.undok.client.mapper.inter.CounselingMapper;
import at.undok.undok.client.mapper.inter.EmployerMapper;
import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.model.entity.*;
import at.undok.undok.client.util.CategoryType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntityToDtoMapper {

    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final CaseMapper caseMapper;
    private final CounselingMapper counselingMapper;
    private final EmployerMapper employerMapper;

    public List<EmployerDto> convertEmployerListToDto(List<Employer> employers) {
        return employerMapper.toDtoList(employers);
    }

    public ClientDto  convertClientToDto(Client client) {
        ClientDto clientDto = mapClientToDto(client);
        // PersonDto personDto = mapPersonToDto(client.getPerson());
        // clientDto.setPerson(personDto);
        if (client.getCounselings() != null) {
            List<CounselingDto> counselingDtos = client.getCounselings().stream()
                                                       .map(this::convertCounselingToDto)
                                                       .toList();
            clientDto.setCounselings(counselingDtos);
        }
        return clientDto;
    }

    public CounselingDto convertCounselingToDto(Counseling counseling) {
        CounselingDto counselingDto = counselingMapper.toDto(counseling);
        return counselingDto;
    }


    public List<ClientDto> convertClientListToDtoList(List<Client> clients) {
        return clients.stream()
                      .map(this::convertClientToDto)
                      .toList();
    }

    public List<AllCounselingDto> convertCounselingListToDtoForTableList(List<Counseling> counselings) {
        return counselings.stream()
                          .map(this::convertCounselingToAllCounselingDto)
                          .toList();
    }

    public List<CounselingDto> convertCounselingListToDtoList(List<Counseling> counselings) {
        List<CounselingDto> dtoList = new ArrayList<>();
        for (Counseling c : counselings) {
            CounselingDto counselingDto = convertCounselingToDto(c);
            dtoList.add(counselingDto);
        }
        return dtoList;
    }

    private AllCounselingDto convertCounselingToAllCounselingDto(Counseling counseling) {
        AllCounselingDto dto = modelMapper.map(counseling, AllCounselingDto.class);

        Client client = counseling.getClient();
        dto.setClientId(client.getId());
        dto.setKeyword(client.getKeyword());
        dto.setClientFullName(buildClientFullName(client));
        dto.setActivityCategories(buildActivityCategoriesString(counseling.getId()));

        return dto;
    }

    private String buildClientFullName(Client client) {
        // Person person = client.getPerson();
        if (client.getFirstName() != null && client.getLastName() != null) {
            return client.getFirstName() + " " + client.getLastName();
        }
        return null;
    }

    private String buildActivityCategoriesString(UUID counselingId) {
        List<CategoryDto> activityCategories =
                categoryService.getCategoryListByTypeAndEntity(CategoryType.LEGAL, counselingId);

        return activityCategories.stream()
                                 .map(CategoryDto::getName)
                                 .collect(java.util.stream.Collectors.joining(","));
    }

    private ClientDto mapClientToDto(Client client) {
        ClientMapperImpl clientMapper = new ClientMapperImpl();
        return clientMapper.toDto(client);
    }


    public EmployerDto mapEmployerToDto(Employer employer) {
        return employerMapper.toDto(employer);
    }

    public Employer mapDtoToEmployer(EmployerDto employerDto) {
        return employerMapper.toEntity(employerDto);
    }


}
