package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.entity.Address;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.repository.AddressRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.PersonRepo;
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
        return createClient(person, client, address, clientForm);
    }

    public Map<String, Map> getClients(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> all = clientRepo.findAll(pageable);

        List<Client> clientList = all.getContent();
        List<ClientDto> clientDtoList = entityToDtoMapper.convertClientListToDtoList(clientList);
        // List<ClientDto> clientDtoList = modelMapper.map(clientList, List.class);

        Map<String, List<ClientDto>> clientMap = new HashMap<>();
        clientMap.put("clientList", clientDtoList);

        Map<String, Map> countAndClientReturnMap = new HashMap<>();

        Map<String, Long> clientCount = new HashMap<>();
        clientCount.put("count", all.getTotalElements());

        countAndClientReturnMap.put("countMap", clientCount);
        countAndClientReturnMap.put("personMap", clientMap);

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
        Person person = client.get().getPerson();
        Address address = person.getAddress();

        return updateClient(person, client.get(), address, clientDto);
    }

    private ClientDto createClient(Person clientPerson, Client client, Address clientAddress, ClientForm clientForm) {

        try {
            clientPerson.setDateOfBirth(toLocalDateService.formatStringToLocalDate(clientForm.getDateOfBirth()));
        } catch (Exception e) {
            clientPerson.setDateOfBirth(null);
        }

        clientPerson.setFirstName(attributeEncryptor.convertToDatabaseColumn(clientForm.getFirstName()));
        clientPerson.setLastName(attributeEncryptor.convertToDatabaseColumn(clientForm.getLastName()));
        clientPerson.setCreatedAt(LocalDateTime.now());

        client.setEducation(clientForm.getEducation());
        client.setKeyword(clientForm.getKeyword());
        client.setHowHasThePersonHeardFromUs(clientForm.getHowHasThePersonHeardFromUs());
        client.setInterpreterNecessary(clientForm.getInterpreterNecessary());
        client.setVulnerableWhenAssertingRights(clientForm.getVulnerableWhenAssertingRights());
        client.setMaritalStatus(clientForm.getMaritalStatus());
        Client savedClient = clientRepo.save(client);

        clientAddress.setStreet(clientForm.getStreet());
        clientAddress.setZipCode(clientForm.getZipCode());
        clientAddress.setCity(clientForm.getCity());
        clientAddress.setCountry(clientForm.getCountry());
        Address address = addressRepo.save(clientAddress);

        clientPerson.setAddress(address);
        clientPerson.setClient(savedClient);
        Person savedPerson = personRepo.save(clientPerson);

        savedClient.setPerson(savedPerson);

        Client c = clientRepo.save(savedClient);
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(c);

        return clientDto;
    }


    private ClientDto updateClient(Person person, Client client, Address address, ClientDto cDto) {
        try {
            person.setDateOfBirth(person.getDateOfBirth());
        } catch (Exception e) {
            person.setDateOfBirth(null);
        }

        person.setFirstName(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getFirstName()));
        person.setLastName(attributeEncryptor.convertToDatabaseColumn(cDto.getPerson().getLastName()));
        person.setUpdatedAt(LocalDateTime.now());

        client.setEducation(cDto.getEducation());
        client.setKeyword(cDto.getKeyword());
        client.setHowHasThePersonHeardFromUs(cDto.getHowHasThePersonHeardFromUs());
        client.setInterpreterNecessary(cDto.getInterpreterNecessary());
        client.setVulnerableWhenAssertingRights(cDto.getVulnerableWhenAssertingRights());
        client.setMaritalStatus(cDto.getMaritalStatus());

        address.setStreet(cDto.getPerson().getAddress().getStreet());
        address.setZipCode(cDto.getPerson().getAddress().getZipCode());
        address.setCity(cDto.getPerson().getAddress().getCity());
        address.setCountry(cDto.getPerson().getAddress().getCountry());

        person.setAddress(address);

        client.setPerson(person);

        Client c = clientRepo.save(client);
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(c);
        return clientDto;
    }

}
