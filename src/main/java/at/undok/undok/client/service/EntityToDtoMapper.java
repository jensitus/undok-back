package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.entity.Employer;
import at.undok.undok.client.model.entity.Person;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityToDtoMapper {

    private final ModelMapper modelMapper;

    private final AttributeEncryptor attributeEncryptor;

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

    private CounselingDto convertCounselingToDto(Counseling counseling) {
        return modelMapper.map(counseling, CounselingDto.class);
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

    public List<CounselingDto> convertCounselingListToDtoList(List<Counseling> counselings) {
        List<CounselingDto> dtoList = new ArrayList<>();
        for (Counseling c : counselings) {
            CounselingDto counselingDto = modelMapper.map(c, CounselingDto.class);
            Client client = c.getClient();
            counselingDto.setClientId(client.getId());
            counselingDto.setClientFullName(attributeEncryptor.convertToEntityAttribute(client.getPerson().getFirstName())
                    + " " + attributeEncryptor.convertToEntityAttribute(client.getPerson().getLastName()));
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
            if (person.getAddress().getCountry() != null) {
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

}
