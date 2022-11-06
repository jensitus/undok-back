package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.model.entity.*;
import at.undok.undok.client.util.CategoryType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityToDtoMapper {

    private final ModelMapper modelMapper;
    private final AttributeEncryptor attributeEncryptor;
    private final CategoryService categoryService;

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
        CounselingDto counselingDto = modelMapper.map(counseling, CounselingDto.class);
        Client client = counseling.getClient();
        counselingDto.setClientId(client.getId());
        counselingDto.setKeyword(client.getKeyword());
        if (client.getPerson().getFirstName() != null && client.getPerson().getLastName() != null) {
            counselingDto.setClientFullName(attributeEncryptor.convertToEntityAttribute(client.getPerson().getFirstName())
                    + " " + attributeEncryptor.convertToEntityAttribute(client.getPerson().getLastName()));
        }
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
            AllCounselingDto counselingDto = modelMapper.map(c, AllCounselingDto.class);
            Client client = c.getClient();
            counselingDto.setClientId(client.getId());
            counselingDto.setKeyword(client.getKeyword());
            if (client.getPerson().getFirstName() != null && client.getPerson().getLastName() != null) {
                counselingDto.setClientFullName(attributeEncryptor.convertToEntityAttribute(client.getPerson().getFirstName())
                        + " " + attributeEncryptor.convertToEntityAttribute(client.getPerson().getLastName()));
            }
            List<CategoryDto> activityCategories = categoryService.getCategoryListByTypeAndEntity(CategoryType.ACTIVITY, c.getId());
            StringBuilder activityCategoriesSeparatedByComma = new StringBuilder();
            activityCategories.forEach(activityCategoryDto -> {
                activityCategoriesSeparatedByComma.append(activityCategoryDto.getName()).append(",");
            });
            String activityCommaCategories = activityCategoriesSeparatedByComma.toString();
            if (!activityCommaCategories.equals("")) {
                activityCommaCategories = activityCommaCategories.substring(0, activityCommaCategories.length() - 1);
            }
            counselingDto.setActivityCategories(activityCommaCategories);
            dtoList.add(counselingDto);
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
        ClientDto clientDto = new ClientDto();

        clientDto.setEducation(client.getEducation());
        clientDto.setId(client.getId());
        clientDto.setHowHasThePersonHeardFromUs(client.getHowHasThePersonHeardFromUs());
        clientDto.setInterpreterNecessary(client.getInterpreterNecessary());
        clientDto.setKeyword(client.getKeyword());
        clientDto.setMaritalStatus(client.getMaritalStatus());
        clientDto.setVulnerableWhenAssertingRights(client.getVulnerableWhenAssertingRights());

        clientDto.setLanguage(client.getLanguage());
        clientDto.setPosition(client.getPosition());
        clientDto.setMembership(client.getMembership());
        clientDto.setOrganization(client.getOrganization());
        clientDto.setNationality(client.getNationality());
        clientDto.setSector(client.getSector());
        clientDto.setUnion(client.getUnion());
        clientDto.setLabourMarketAccess(client.getLabourMarketAccess());
        clientDto.setCurrentResidentStatus(client.getCurrentResidentStatus());

        return clientDto;
    }

    private PersonDto mapPersonToDto(Person person) {
        PersonDto personDto = new PersonDto();

        if (person == null) {
            return null;
        }
        if (person.getFirstName() != null) {
            personDto.setFirstName(attributeEncryptor.convertToEntityAttribute(person.getFirstName()));
        }
        if (person.getLastName() != null) {
            personDto.setLastName(attributeEncryptor.convertToEntityAttribute(person.getLastName()));
        }
        if (person.getEmail() != null) {
            personDto.setEmail(attributeEncryptor.convertToEntityAttribute(person.getEmail()));
        }
        if (person.getTelephone() != null) {
            personDto.setTelephone(attributeEncryptor.convertToEntityAttribute(person.getTelephone()));
        }
        if (person.getGender() != null) {
            personDto.setGender(attributeEncryptor.convertToEntityAttribute(person.getGender()));
        }

        personDto.setId(person.getId());
        personDto.setDateOfBirth(person.getDateOfBirth());

        if (person.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setId(person.getAddress().getId());
            if (person.getAddress().getCity() != null) {
                addressDto.setCity(attributeEncryptor.convertToEntityAttribute(person.getAddress().getCity()));
            }
            if (person.getAddress().getStreet() != null) {
                addressDto.setStreet(attributeEncryptor.convertToEntityAttribute(person.getAddress().getStreet()));
            }
            if (person.getAddress().getZipCode() != null) {
                addressDto.setZipCode(attributeEncryptor.convertToEntityAttribute(person.getAddress().getZipCode()));
            }
            if (person.getAddress().getCountry() != null) {
                addressDto.setCountry(attributeEncryptor.convertToEntityAttribute(person.getAddress().getCountry()));
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
            employerPerson.setFirstName(attributeEncryptor.convertToDatabaseColumn(employerDto.getPerson().getFirstName()));
        }
        if (employerDto.getPerson().getLastName() != null) {
            employerPerson.setLastName(attributeEncryptor.convertToDatabaseColumn(employerDto.getPerson().getLastName()));
        }
        if (employerDto.getPerson().getEmail() != null) {
            employerPerson.setEmail(attributeEncryptor.convertToDatabaseColumn(employerDto.getPerson().getEmail()));
        }
        if (employerDto.getPerson().getTelephone() != null) {
            employerPerson.setTelephone(attributeEncryptor.convertToDatabaseColumn(employerDto.getPerson().getTelephone()));
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
            address.setStreet(attributeEncryptor.convertToDatabaseColumn(addressDto.getStreet()));
        }
        if (addressDto.getCity() != null) {
            address.setCity(attributeEncryptor.convertToDatabaseColumn(addressDto.getCity()));
        }
        if (addressDto.getZipCode() != null) {
            address.setZipCode(attributeEncryptor.convertToDatabaseColumn(addressDto.getZipCode()));
        }
        if (addressDto.getCountry() != null) {
            address.setCountry(attributeEncryptor.convertToDatabaseColumn(addressDto.getCountry()));
        }
        return address;
    }

}
