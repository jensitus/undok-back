package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.entity.Address;
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

import java.time.LocalDateTime;
import java.util.*;

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
        Address address = new Address();
        return createClient(person, client, address, clientForm);
    }

    public Map<String, Map> getClients(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> all = personRepo.findAll(pageable);

        List<Person> personList = all.getContent();
        List<PersonDto> personDtoList = entityToDtoMapper.convertPersonListToDtoList(personList);

        Map<String, List<PersonDto>> personMap = new HashMap<>();
        personMap.put("personList", personDtoList);

        Map<String, Map> countAndClientReturnMap = new HashMap<>();

        Map<String, Long> clientCount = new HashMap<>();
        clientCount.put("count", all.getTotalElements());

        countAndClientReturnMap.put("countMap", clientCount);
        countAndClientReturnMap.put("personMap", personMap);

        return countAndClientReturnMap;
    }

    public PersonDto getClientById(UUID id) {
        Optional<Person> personOptional = personRepo.findById(id);
        return entityToDtoMapper.convertPersonToDto(personOptional.get());
    }

    public Long getNumberOfClients() {
        return clientRepo.count();
    }

    public ClientDto updateClient(UUID clientId, PersonDto personDto) {

        Client client = clientRepo.getOne(clientId);
        Person person = client.getPerson();
        Address address = person.getAddress();

        return updateClient(person, client, address, personDto);
    }

    private ClientDto createClient(Person person, Client client, Address address, ClientForm clientForm) {

        try {
            person.setDateOfBirth(toLocalDateService.formatStringToLocalDate(clientForm.getDateOfBirth()));
        } catch (Exception e) {
            person.setDateOfBirth(null);
        }

        person.setFirstName(attributeEncryptor.convertToDatabaseColumn(clientForm.getFirstName()));
        person.setLastName(attributeEncryptor.convertToDatabaseColumn(clientForm.getLastName()));
        person.setCreatedAt(LocalDateTime.now());

        client.setEducation(clientForm.getEducation());
        client.setKeyword(clientForm.getKeyword());
        client.setHowHasThePersonHeardFromUs(clientForm.getHowHasThePersonHeardFromUs());
        client.setInterpreterNecessary(clientForm.getInterpreterNecessary());
        client.setVulnerableWhenAssertingRights(clientForm.getVulnerableWhenAssertingRights());
        client.setMaritalStatus(clientForm.getMaritalStatus());

        address.setStreet(clientForm.getStreet());
        address.setZipCode(clientForm.getZipCode());
        address.setCity(clientForm.getCity());
        address.setCountry(clientForm.getCountry());

        person.setAddress(address);

        client.setPerson(person);

        Client c = clientRepo.save(client);
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(c);
        return clientDto;
    }

    private ClientDto updateClient(Person person, Client client, Address address, PersonDto personDto) {
        try {
            person.setDateOfBirth(person.getDateOfBirth());
        } catch (Exception e) {
            person.setDateOfBirth(null);
        }

        person.setFirstName(attributeEncryptor.convertToDatabaseColumn(personDto.getFirstName()));
        person.setLastName(attributeEncryptor.convertToDatabaseColumn(personDto.getLastName()));
        person.setUpdatedAt(LocalDateTime.now());

        client.setEducation(personDto.getClient().getEducation());
        client.setKeyword(personDto.getClient().getKeyword());
        client.setHowHasThePersonHeardFromUs(personDto.getClient().getHowHasThePersonHeardFromUs());
        client.setInterpreterNecessary(personDto.getClient().getInterpreterNecessary());
        client.setVulnerableWhenAssertingRights(personDto.getClient().getVulnerableWhenAssertingRights());
        client.setMaritalStatus(personDto.getClient().getMaritalStatus());

        address.setStreet(personDto.getAddress().getStreet());
        address.setZipCode(personDto.getAddress().getZipCode());
        address.setCity(personDto.getAddress().getCity());
        address.setCountry(personDto.getAddress().getCountry());

        person.setAddress(address);

        client.setPerson(person);

        Client c = clientRepo.save(client);
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(c);
        return clientDto;
    }

}
