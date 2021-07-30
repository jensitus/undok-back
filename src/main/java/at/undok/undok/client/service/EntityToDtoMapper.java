package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.AddressDto;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.entity.Address;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.entity.Person;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntityToDtoMapper {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AttributeEncryptor attributeEncryptor;

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

//    public List<ClientDto> convertClientListToDtoList(List<Client> clients) {
//        List<ClientDto> clientDtos = new ArrayList<>();
//        for (Client c : clients) {
//            clientDtos.add(convertClientToDto(c));
//        }
//        return clientDtos;
//    }

    public PersonDto convertPersonToDto(Person person) {
        return mapPersonToDto(person);
    }

    public List<ClientDto> convertClientListToDtoList(List<Client> clients) {
        List<ClientDto> clientDtos = new ArrayList<>();
        for (Client c : clients) {
            Person p = c.getPerson();
            ClientDto clientDto = convertClientToDto(c);
            PersonDto personDto = convertPersonToDto(p);
            clientDto.setPerson(personDto);
            clientDtos.add(clientDto);
        }
        return clientDtos;
    }

    public List<CounselingDto> convertCounselingListToDtoList(List<Counseling> counselings) {
        List<CounselingDto> dtoList = new ArrayList<>();
        for (Counseling c : counselings) {
            CounselingDto counselingDto = modelMapper.map(c, CounselingDto.class);
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

        return clientDto;
    }

    private PersonDto mapPersonToDto(Person person) {
        PersonDto personDto = new PersonDto();

        String firstName = attributeEncryptor.convertToEntityAttribute(person.getFirstName());
        String lastName = attributeEncryptor.convertToEntityAttribute(person.getLastName());

        personDto.setFirstName(firstName);
        personDto.setLastName(lastName);
        personDto.setId(person.getId());
        personDto.setDateOfBirth(person.getDateOfBirth());

        if (person.getAddress() != null) {
            AddressDto addressDto = modelMapper.map(person.getAddress(), AddressDto.class);
            personDto.setAddress(addressDto);
        }

        return personDto;
    }

}
