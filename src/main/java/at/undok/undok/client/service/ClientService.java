package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.exception.KeywordNotFoundException;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.entity.Address;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.repository.AddressRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.PersonRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class ClientService {

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private ToLocalDateService toLocalDateService;

    @Autowired
    private AttributeEncryptor attributeEncryptor;

    public ClientDto createClient(ClientForm clientForm) {
        log.info(clientForm.toString());

        Client client = new Client();
        Person person = new Person();
        Address address = new Address();
        return createTheCompleteClient(clientForm);
    }

    public Map<String, Map> getClients(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> all = clientRepo.findAll(pageable);

        List<Client> clientList = all.getContent();
        List<ClientDto> clientDtoList = entityToDtoMapper.convertClientListToDtoList(clientList);

        Map<String, List<ClientDto>> clientMap = new HashMap<>();
        clientMap.put("clientList", clientDtoList);

        Map<String, Map> countAndClientReturnMap = new HashMap<>();

        Map<String, Long> clientCount = new HashMap<>();
        clientCount.put("count", all.getTotalElements());

        countAndClientReturnMap.put("countMap", clientCount);
        countAndClientReturnMap.put("clientMap", clientMap);

        return countAndClientReturnMap;
    }

    public ClientDto getClientById(UUID id) {
        Optional<Client> personOptional = clientRepo.findById(id);
        return entityToDtoMapper.convertClientToDto(personOptional.get());
    }

    public Long getNumberOfClients() {
        return clientRepo.count();
    }

    public ClientDto updateClient(UUID clientId, ClientDto clientDto) {

        Optional<Client> client = clientRepo.findById(clientId);
        if (client.isPresent()) {
            Person person = client.get().getPerson();
            Address address = person.getAddress();
            return updateClient(person, client.get(), address, clientDto);
        } else {
            throw new RuntimeException("Sorry");
        }
    }

    private ClientDto createTheCompleteClient(ClientForm clientForm) {

        Person clientPerson = new Person();
        Client client = new Client();
        Address clientAddress = new Address();

        try {
            clientPerson.setDateOfBirth(toLocalDateService.formatStringToLocalDate(clientForm.getDateOfBirth()));
        } catch (Exception e) {
            clientPerson.setDateOfBirth(null);
        }

        if (clientForm.getFirstName() != null) {
            clientPerson.setFirstName(attributeEncryptor.convertToDatabaseColumn(clientForm.getFirstName()));
        }
        if (clientForm.getLastName() != null) {
            clientPerson.setLastName(attributeEncryptor.convertToDatabaseColumn(clientForm.getLastName()));
        }
        clientPerson.setCreatedAt(LocalDateTime.now());

        client.setEducation(clientForm.getEducation());
        client.setKeyword(clientForm.getKeyword());
        client.setHowHasThePersonHeardFromUs(clientForm.getHowHasThePersonHeardFromUs());
        client.setInterpreterNecessary(clientForm.getInterpreterNecessary());
        client.setVulnerableWhenAssertingRights(clientForm.getVulnerableWhenAssertingRights());
        client.setMaritalStatus(clientForm.getMaritalStatus());
        client.setCurrentResidentStatus(clientForm.getCurrentResidentStatus());
        client.setLabourMarketAccess(clientForm.getLabourMarketAccess());
        client.setLanguage(clientForm.getLanguage());
        client.setUnion(clientForm.getUnion());
        client.setMembership(clientForm.getMembership());
        client.setNationality(clientForm.getNationality());
        client.setSector(clientForm.getSector());
        client.setOrganization(clientForm.getOrganization());
        client.setPosition(clientForm.getPosition());
        Client saveAndFlush = clientRepo.saveAndFlush(client);

        if (clientForm.getStreet() != null) {
            clientAddress.setStreet(attributeEncryptor.convertToDatabaseColumn(clientForm.getStreet()));
        }
        if (clientForm.getZipCode() != null) {
            clientAddress.setZipCode(attributeEncryptor.convertToDatabaseColumn(clientForm.getZipCode()));
        }
        if (clientForm.getCity() != null) {
            clientAddress.setCity(attributeEncryptor.convertToDatabaseColumn(clientForm.getCity()));
        }
        if (clientForm.getCountry() != null) {
            clientAddress.setCountry(attributeEncryptor.convertToDatabaseColumn(clientForm.getCountry()));
        }
        Address savedAddress = addressRepo.save(clientAddress);

        clientPerson.setAddress(savedAddress);
        clientPerson.setClient(saveAndFlush);
        Person savedPerson = personRepo.save(clientPerson);

        client.setPerson(savedPerson);

        Client c = clientRepo.save(client);
        return entityToDtoMapper.convertClientToDto(c);
    }


    private ClientDto updateClient(Person person, Client client, Address address, ClientDto cDto) {
        try {
            person.setDateOfBirth(person.getDateOfBirth());
        } catch (Exception e) {
            person.setDateOfBirth(null);
        }

        if (cDto.getPerson().getFirstName() != null) {
            person.setFirstName(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getFirstName()));
        }
        if (cDto.getPerson().getLastName() != null) {
            person.setLastName(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getLastName()));
        }
        person.setUpdatedAt(LocalDateTime.now());

        client.setEducation(cDto.getEducation());
        client.setKeyword(cDto.getKeyword());
        client.setHowHasThePersonHeardFromUs(cDto.getHowHasThePersonHeardFromUs());
        client.setInterpreterNecessary(cDto.getInterpreterNecessary());
        client.setVulnerableWhenAssertingRights(cDto.getVulnerableWhenAssertingRights());
        client.setMaritalStatus(cDto.getMaritalStatus());
        client.setCurrentResidentStatus(cDto.getCurrentResidentStatus());
        client.setLabourMarketAccess(cDto.getLabourMarketAccess());
        client.setLanguage(cDto.getLanguage());
        client.setUnion(cDto.getUnion());
        client.setMembership(cDto.getMembership());
        client.setNationality(cDto.getNationality());
        client.setSector(cDto.getSector());
        client.setOrganization(cDto.getOrganization());
        client.setPosition(cDto.getPosition());

        if (cDto.getPerson().getAddress().getStreet() != null) {
            address.setStreet(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getAddress().getStreet()));
        }
        if (cDto.getPerson().getAddress().getZipCode() != null) {
            address.setZipCode(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getAddress().getZipCode()));
        }
        if (cDto.getPerson().getAddress().getCity() != null) {
            address.setCity(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getAddress().getCity()));
        }
        if (cDto.getPerson().getAddress().getCountry() != null) {
            address.setCountry(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getAddress().getCountry()));
        }

        person.setAddress(address);

        client.setPerson(person);

        Client c = clientRepo.save(client);
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(c);
        return clientDto;
    }

}
