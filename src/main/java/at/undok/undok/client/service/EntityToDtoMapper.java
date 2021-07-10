package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.PersonDto;
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
        ClientDto clientDto = modelMapper.map(client, ClientDto.class);
        List<CounselingDto> counselingDtos = new ArrayList<>();
        for (Counseling counseling : client.getCounselings()) {
            CounselingDto counselingDto = convertCounselingToDto(counseling);
            counselingDtos.add(counselingDto);
        }
        clientDto.setCounselings(counselingDtos);
        return clientDto;
    }

    private CounselingDto convertCounselingToDto(Counseling counseling) {
        return modelMapper.map(counseling, CounselingDto.class);
    }

    public List<ClientDto> convertClientListToDtoList(List<Client> clients) {
        List<ClientDto> clientDtos = new ArrayList<>();
        for (Client c : clients) {
            clientDtos.add(convertClientToDto(c));
        }
        return clientDtos;
    }

    public PersonDto convertPersonToDto(Person person) {
        PersonDto personDto = new PersonDto();
        ClientDto clientDto = convertClientToDto(person.getClient());
        String firstName = attributeEncryptor.convertToEntityAttribute(person.getFirstName());
        String lastName = attributeEncryptor.convertToEntityAttribute(person.getLastName());
        personDto.setFirstName(firstName);
        personDto.setLastName(lastName);
        personDto.setId(person.getId());
        personDto.setDateOfBirth(person.getDateOfBirth());
        personDto.setClient(clientDto);
        return personDto;
    }

    public List<PersonDto> convertPersonListToDtoList(List<Person> persons) {
        List<PersonDto> personDtos = new ArrayList<>();
        for (Person p : persons) {
            Client client = p.getClient();
            ClientDto clientDto = convertClientToDto(client);
            PersonDto personDto = convertPersonToDto(p);
            personDto.setClient(clientDto);
            personDtos.add(personDto);
        }
        return personDtos;
    }

}
