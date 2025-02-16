package at.undok.undok.client.service;

import at.undok.undok.client.mapper.impl.ClientMapperImpl;
import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.mapper.inter.ClientMapper;
import at.undok.undok.client.mapper.inter.CounselingMapper;
import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.model.entity.*;
import at.undok.undok.client.util.CategoryType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityToDtoMapper {

    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final CaseMapper caseMapper;
    private final CounselingMapper counselingMapper;

    public List<EmployerDto> convertEmployerListToDto(List<Employer> employers) {
        List<EmployerDto> employerDtoList = new ArrayList<>();
        for (Employer e : employers) {
            EmployerDto employerDto = mapEmployerToDto(e);
            employerDtoList.add(employerDto);
        }
        return employerDtoList;
    }

    public ClientDto convertClientToDto(Client client) {
        ClientDto clientDto = mapClientToDto(client);
        PersonDto personDto = mapPersonToDto(client.getPerson());
        clientDto.setPerson(personDto);
        if (client.getCounselings() != null) {
            List<CounselingDto> counselingDtos = new ArrayList<>();
            for (Counseling counseling : client.getCounselings()) {
                CounselingDto counselingDto = convertCounselingToDto(counseling);
                counselingDtos.add(counselingDto);
            }
            clientDto.setCounselings(counselingDtos);
        }
        return clientDto;
    }

    public CounselingDto convertCounselingToDto(Counseling counseling) {
        CounselingDto counselingDto = counselingMapper.toDto(counseling);
        CaseDto caseDto = caseMapper.toDto(counseling.getCounselingCase());
        counselingDto.setCounselingCase(caseDto);
        return counselingDto;
    }

    public PersonDto convertPersonToDto(Person person) {
        return mapPersonToDto(person);
    }

    public List<ClientDto> convertClientListToDtoList(List<Client> clients) {
        List<ClientDto> clientDtos = new ArrayList<>();
        for (Client c : clients) {
            Person p = c.getPerson();
            ClientDto clientDto = convertClientToDto(c);
            PersonDto personDto = mapPersonToDto(p);
            clientDto.setPerson(personDto);
            clientDtos.add(clientDto);
        }
        return clientDtos;
    }

    public List<AllCounselingDto> convertCounselingListToDtoForTableList(List<Counseling> counselings) {
        List<AllCounselingDto> dtoList = new ArrayList<>();
        for (Counseling c : counselings) {
            AllCounselingDto allCounselingDto = modelMapper.map(c, AllCounselingDto.class);
            Client client = c.getClient();
            allCounselingDto.setClientId(client.getId());
            allCounselingDto.setKeyword(client.getKeyword());
            if (client.getPerson().getFirstName() != null && client.getPerson().getLastName() != null) {
                allCounselingDto.setClientFullName(client.getPerson().getFirstName()
                        + " " + client.getPerson().getLastName());
            }
            List<CategoryDto> activityCategories = categoryService.getCategoryListByTypeAndEntity(CategoryType.LEGAL, c.getId());
            StringBuilder activityCategoriesSeparatedByComma = new StringBuilder();
            activityCategories.forEach(activityCategoryDto -> {
                activityCategoriesSeparatedByComma.append(activityCategoryDto.getName()).append(",");
            });
            String activityCommaCategories = activityCategoriesSeparatedByComma.toString();
            if (!activityCommaCategories.equals("")) {
                activityCommaCategories = activityCommaCategories.substring(0, activityCommaCategories.length() - 1);
            }
            allCounselingDto.setActivityCategories(activityCommaCategories);
            dtoList.add(allCounselingDto);
        }
        return dtoList;
    }

    public List<CounselingDto> convertCounselingListToDtoList(List<Counseling> counselings) {
        List<CounselingDto> dtoList = new ArrayList<>();
        for (Counseling c : counselings) {
            CounselingDto counselingDto = convertCounselingToDto(c);
            dtoList.add(counselingDto);
        }
        return dtoList;
    }

    private ClientDto mapClientToDto(Client client) {
        ClientMapperImpl clientMapper = new ClientMapperImpl();
        return clientMapper.toDto(client);
    }

    private PersonDto mapPersonToDto(Person person) {
        PersonDto personDto = new PersonDto();

        if (person == null) {
            return null;
        }
        if (person.getFirstName() != null) {
            personDto.setFirstName(person.getFirstName());
        }
        if (person.getLastName() != null) {
            personDto.setLastName(person.getLastName());
        }
        if (person.getEmail() != null) {
            personDto.setEmail(person.getEmail());
        }
        if (person.getTelephone() != null) {
            personDto.setTelephone(person.getTelephone());
        }
        if (person.getGender() != null) {
            personDto.setGender(person.getGender());
        }

        personDto.setId(person.getId());
        personDto.setDateOfBirth(person.getDateOfBirth());

        if (person.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setId(person.getAddress().getId());
            if (person.getAddress().getCity() != null) {
                addressDto.setCity(person.getAddress().getCity());
            }
            if (person.getAddress().getStreet() != null) {
                addressDto.setStreet(person.getAddress().getStreet());
            }
            if (person.getAddress().getZipCode() != null) {
                addressDto.setZipCode(person.getAddress().getZipCode());
            }
            if (person.getAddress().getCountry() != null) {
                addressDto.setCountry(person.getAddress().getCountry());
            }
            personDto.setAddress(addressDto);
        }

        return personDto;
    }

    public EmployerDto mapEmployerToDto(Employer employer) {
        EmployerDto employerDto = new EmployerDto();
        employerDto.setCompany(employer.getCompany());
        employerDto.setPosition(employer.getPosition());
        employerDto.setId(employer.getId());
        Person person = employer.getPerson();
        PersonDto personDto = mapPersonToDto(person);
        employerDto.setPerson(personDto);
        return employerDto;
    }

    public Employer mapDtoToEmployer(EmployerDto employerDto) {
        Person employerPerson = new Person();


        if (employerDto.getPerson().getFirstName() != null) {
            employerPerson.setFirstName(employerDto.getPerson().getFirstName());
        }
        if (employerDto.getPerson().getLastName() != null) {
            employerPerson.setLastName(employerDto.getPerson().getLastName());
        }
        if (employerDto.getPerson().getEmail() != null) {
            employerPerson.setEmail(employerDto.getPerson().getEmail());
        }
        if (employerDto.getPerson().getTelephone() != null) {
            employerPerson.setTelephone(employerDto.getPerson().getTelephone());
        }


        Employer employer = new Employer();
        employer.setCompany(employerDto.getCompany());
        employer.setPosition(employerDto.getPosition());
        employer.setPerson(employerPerson);

        return employer;
    }

    public Address mapToAddress(AddressDto addressDto) {
        Address address = new Address();
        if (addressDto.getId() != null) {
            address.setId(addressDto.getId());
        }
        if (addressDto.getStreet() != null) {
            address.setStreet(addressDto.getStreet());
        }
        if (addressDto.getCity() != null) {
            address.setCity(addressDto.getCity());
        }
        if (addressDto.getZipCode() != null) {
            address.setZipCode(addressDto.getZipCode());
        }
        if (addressDto.getCountry() != null) {
            address.setCountry(addressDto.getCountry());
        }
        return address;
    }

}
