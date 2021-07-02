package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.PersonRepo;
import at.undok.undok.client.util.MaritalStatusConverter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ClientService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private ToLocalDateService toLocalDateService;

    @Autowired
    private MaritalStatusConverter maritalStatusConverter;

    @Autowired
    private AttributeEncryptor attributeEncryptor;

    public ClientDto createClient(ClientForm clientForm) {
        log.info(clientForm.toString());

        Client client = new Client();
        Person person = new Person();
        person.setDateOfBirth(toLocalDateService.formatStringToLocalDate(clientForm.getDateOfBirth()));
        person.setFirstName(attributeEncryptor.convertToDatabaseColumn(clientForm.getFirstName()));
        person.setLastName(attributeEncryptor.convertToDatabaseColumn(clientForm.getLastName()));
        client.setEducation(clientForm.getEducation());
        client.setKeyword(clientForm.getKeyword());
        client.setHowHasThePersonHeardFromUs(clientForm.getHowHasThePersonHeardFromUs());
        client.setInterpreterNecessary(clientForm.getInterpreterNecessary());
        client.setVulnerableWhenAssertingRights(clientForm.getVulnerableWhenAssertingRights());
        client.setMaritalStatus(clientForm.getMaritalStatus());

        client.setPerson(person);

        Client c = clientRepo.save(client);
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(c);
        return clientDto;
    }

    public List<PersonDto> getClients(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> all = personRepo.findAll(pageable);
        List<Person> personList = all.getContent();
        return entityToDtoMapper.convertPersonListToDtoList(personList);
    }

    public PersonDto getClientById(UUID id) {
        Optional<Person> personOptional = personRepo.findById(id);
        PersonDto personDto = entityToDtoMapper.convertPersonToDto(personOptional.get());
        return personDto;
    }

}
