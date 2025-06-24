package at.undok.undok.client.service;

import at.undok.common.util.ToLocalDateService;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.entity.*;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.JoinCategoryForm;
import at.undok.undok.client.repository.AddressRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.PersonRepo;
import at.undok.undok.client.util.CategoryType;
import at.undok.undok.client.util.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class ClientService {

    private final EntityToDtoMapper entityToDtoMapper;
    private final ClientRepo clientRepo;
    private final PersonRepo personRepo;
    private final AddressRepo addressRepo;
    private final ToLocalDateService toLocalDateService;
    private final CounselingService counselingService;
    private final CaseService caseService;
    private final CategoryService categoryService;

    public ClientService(EntityToDtoMapper entityToDtoMapper, ClientRepo clientRepo, PersonRepo personRepo, AddressRepo addressRepo, ToLocalDateService toLocalDateService, CounselingService counselingService, CaseService caseService, CategoryService categoryService) {
        this.entityToDtoMapper = entityToDtoMapper;
        this.clientRepo = clientRepo;
        this.personRepo = personRepo;
        this.addressRepo = addressRepo;
        this.toLocalDateService = toLocalDateService;
        this.counselingService = counselingService;
        this.caseService = caseService;
        this.categoryService = categoryService;
    }

    public boolean checkIfKeywordAlreadyExists(String keyword) {
        return clientRepo.existsByKeyword(keyword);
    }

    public List<AllClientDto> getAll() {
        List<ClientDto> clientDtos =
                entityToDtoMapper.convertClientListToDtoList(
                        clientRepo.findByStatusOrderByCreatedAtDesc(StatusService.STATUS_ACTIVE));  // findAll(Sort.by(Sort.Order.by("createdAt"))));
        List<AllClientDto> allClientDtoList = turnClientDtoListToAllClientDtoList(clientDtos);
        return allClientDtoList;
    }

    public ClientDto createClient(ClientForm clientForm) {
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
        Client client = clientRepo.findById(id).orElseThrow();
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(client);
        List<CaseDto> openCaseList = caseService.getCaseByClientIdAndStatus(client.getId(), "OPEN");
        clientDto.setOpenCase(openCaseList.size() == 1 ? openCaseList.get(0) : null);
        List<CaseDto> closeCaseList = caseService.getCaseByClientIdAndStatus(client.getId(), "CLOSED");
        clientDto.setClosedCases(!closeCaseList.isEmpty() ? closeCaseList : null);
        List<CategoryDto> jobFunctionList = categoryService.getCategoryListByTypeAndEntity(CategoryType.JOB_FUNCTION, client.getId());
        clientDto.setJobFunctions(jobFunctionList);
        return clientDto;
    }

    public Long getNumberOfClients() {
        return clientRepo.countByStatus(StatusService.STATUS_ACTIVE);
    }

    public ClientDto updateClient(UUID clientId, ClientForm clientForm) {

        Optional<Client> client = clientRepo.findById(clientId);
        if (client.isPresent()) {
            Person person = client.get().getPerson();
            Address address = person.getAddress();
            return updateClient(person, client.get(), address, clientForm, clientId);
        } else {
            throw new RuntimeException("Sorry");
        }
    }

    public void setStatusDeleted(UUID clientPersonId) {
        Optional<Person> optionalPerson = personRepo.findById(clientPersonId);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            Client client = person.getClient();
            client.setStatus(StatusService.STATUS_DELETED);
            clientRepo.save(client);
            List<Counseling> counselings = client.getCounselings();
            List<UUID> counselingIds = new ArrayList<>();
            counselings.forEach(c -> {
                counselingIds.add(c.getId());
            });
            counselingService.setStatusDeleted(counselingIds);
        } else {
            throw new NoSuchElementException("No client found with ID " + clientPersonId);
        }
    }

    public void deleteClient(UUID clientId) {
        personRepo.deleteById(clientId);
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
            clientPerson.setFirstName(clientForm.getFirstName());
        }
        if (clientForm.getLastName() != null) {
            clientPerson.setLastName(clientForm.getLastName());
        }
        if (clientForm.getEmail() != null) {
            clientPerson.setEmail(clientForm.getEmail());
        }
        if (clientForm.getTelephone() != null) {
            clientPerson.setTelephone(clientForm.getTelephone());
        }
        if (clientForm.getGender() != null) {
            clientPerson.setGender(clientForm.getGender());
        }
        clientPerson.setCreatedAt(LocalDateTime.now());

        setClient(client, clientForm.getEducation(), clientForm.getKeyword(), clientForm.getHowHasThePersonHeardFromUs(), clientForm.getInterpreterNecessary(), clientForm.getVulnerableWhenAssertingRights(), clientForm.getMaritalStatus(), clientForm.getCurrentResidentStatus(), clientForm.getLabourMarketAccess(), clientForm.getLanguage(), clientForm.getUnion(), clientForm.getMembership(), clientForm.getNationality(), clientForm.getSector(), clientForm.getOrganization(), clientForm.getPosition());
        client.setCreatedAt(LocalDateTime.now());
        client.setSocialInsuranceNumber(clientForm.getSocialInsuranceNumber());
        client.setStatus(StatusService.STATUS_ACTIVE);
        Client saveAndFlush = clientRepo.saveAndFlush(client);

        if (clientForm.getStreet() != null) {
            clientAddress.setStreet(clientForm.getStreet());
        }
        if (clientForm.getZipCode() != null) {
            clientAddress.setZipCode(clientForm.getZipCode());
        }
        if (clientForm.getCity() != null) {
            clientAddress.setCity(clientForm.getCity());
        }
        if (clientForm.getCountry() != null) {
            clientAddress.setCountry(clientForm.getCountry());
        }
        Address savedAddress = addressRepo.save(clientAddress);

        clientPerson.setAddress(savedAddress);
        clientPerson.setClient(saveAndFlush);
        Person savedPerson = personRepo.save(clientPerson);

        client.setPerson(savedPerson);

        Client c = clientRepo.save(client);
        ClientDto clientDto = entityToDtoMapper.convertClientToDto(c);
        CaseDto caseDto = new CaseDto();
        caseDto.setClientId(clientDto.getId());
        caseService.createCase(clientDto.getId(), clientDto.getKeyword(), clientForm.getTargetGroup(), clientForm.getHumanTrafficking(), clientForm.getJobCenterBlock(), clientForm.getWorkingRelationship());
        return clientDto;
    }

    private void setClient(Client client, String education, String keyword, String howHasThePersonHeardFromUs, Boolean interpreterNecessary, Boolean vulnerableWhenAssertingRights, String maritalStatus, String currentResidentStatus, String labourMarketAccess, String language, String union, Boolean membership, String nationality, String sector, String organization, String position) {
        client.setEducation(education);
        client.setKeyword(keyword);
        client.setHowHasThePersonHeardFromUs(howHasThePersonHeardFromUs);
        client.setInterpreterNecessary(interpreterNecessary);
        client.setVulnerableWhenAssertingRights(vulnerableWhenAssertingRights);
        client.setMaritalStatus(maritalStatus);
        client.setCurrentResidentStatus(currentResidentStatus);
        client.setLabourMarketAccess(labourMarketAccess);
        client.setLanguage(language);
        client.setUnion(union);
        client.setMembership(membership);
        client.setNationality(nationality);
        client.setSector(sector);
        client.setOrganization(organization);
        client.setPosition(position);
    }


    private ClientDto updateClient(Person person, Client client, Address address, ClientForm clientForm, UUID clientId) {

        if (clientForm.getFirstName() != null) {
            person.setFirstName(clientForm.getFirstName());
        }
        if (clientForm.getLastName() != null) {
            person.setLastName(clientForm.getLastName());
        }
        if (clientForm.getEmail() != null) {
            person.setEmail(clientForm.getEmail());
        }
        if (clientForm.getTelephone() != null) {
            person.setTelephone(clientForm.getTelephone());
        }
        if (clientForm.getGender() != null) {
            person.setGender(clientForm.getGender());
        }
        person.setUpdatedAt(LocalDateTime.now());

        setClient(client, clientForm.getEducation(), clientForm.getKeyword(), clientForm.getHowHasThePersonHeardFromUs(), clientForm.getInterpreterNecessary(), clientForm.getVulnerableWhenAssertingRights(), clientForm.getMaritalStatus(), clientForm.getCurrentResidentStatus(), clientForm.getLabourMarketAccess(), clientForm.getLanguage(), clientForm.getUnion(), clientForm.getMembership(), clientForm.getNationality(), clientForm.getSector(), clientForm.getOrganization(), clientForm.getPosition());
        client.setUpdatedAt(LocalDateTime.now());
        client.setSocialInsuranceNumber(clientForm.getSocialInsuranceNumber());

        if (clientForm.getStreet() != null) {
            address.setStreet(clientForm.getStreet());
        }
        if (clientForm.getZipCode() != null) {
            address.setZipCode(clientForm.getZipCode());
        }
        if (clientForm.getCity() != null) {
            address.setCity(clientForm.getCity());
        }
        if (clientForm.getCountry() != null) {
            address.setCountry(clientForm.getCountry());
        }

        person.setAddress(address);
        client.setPerson(person);
        Client c = clientRepo.save(client);
        CaseDto caseDto = caseService.updateCase(clientId,
                                                 clientForm.getWorkingRelationship(),
                                                 clientForm.getHumanTrafficking(),
                                                 clientForm.getJobCenterBlock(),
                                                 clientForm.getTargetGroup());
        categoryService.sortOutDeselected(clientForm.getCounselingLanguageSelected(),
                                          CategoryType.COUNSELING_LANGUAGE,
                                          caseDto.getId());
        categoryService.sortOutDeselected(clientForm.getJobMarketAccessSelected(),
                                          CategoryType.JOB_MARKET_ACCESS,
                                          caseDto.getId());
        categoryService.sortOutDeselected(clientForm.getOriginOfAttentionSelected(),
                                          CategoryType.ORIGIN_OF_ATTENTION,
                                          caseDto.getId());
        categoryService.sortOutDeselected(clientForm.getUndocumentedWorkSelected(),
                                          CategoryType.UNDOCUMENTED_WORK,
                                          caseDto.getId());
        categoryService.sortOutDeselected(clientForm.getComplaintsSelected(),
                                          CategoryType.COMPLAINT,
                                          caseDto.getId());
        return entityToDtoMapper.convertClientToDto(c);
    }

    private List<AllClientDto> turnClientDtoListToAllClientDtoList(List<ClientDto> clientDtoList) {
        List<AllClientDto> allClientDtoList = new ArrayList<>();

        for (ClientDto clientDto : clientDtoList) {
            AllClientDto allClientDto = new AllClientDto();
            allClientDto.setId(clientDto.getId());
            // person stuff:
            allClientDto.setFirstName(clientDto.getPerson().getFirstName());
            allClientDto.setLastName(clientDto.getPerson().getLastName());
            allClientDto.setDateOfBirth(clientDto.getPerson().getDateOfBirth());
            allClientDto.setEmail(clientDto.getPerson().getEmail());
            allClientDto.setTelephone(clientDto.getPerson().getTelephone());
            allClientDto.setGender(clientDto.getPerson().getGender());
            // address stuff:
            allClientDto.setStreet(clientDto.getPerson().getAddress().getStreet());
            allClientDto.setZipCode(clientDto.getPerson().getAddress().getZipCode());
            allClientDto.setCity(clientDto.getPerson().getAddress().getCity());
            allClientDto.setCountry(clientDto.getPerson().getAddress().getCountry());
            // client stuff:
            allClientDto.setKeyword(clientDto.getKeyword());
            allClientDto.setEducation(clientDto.getEducation());
            allClientDto.setLanguage(clientDto.getLanguage());
            allClientDto.setSector(clientDto.getSector());
            allClientDto.setUnion(clientDto.getUnion());
            allClientDto.setMembership(clientDto.getMembership());
            allClientDto.setPosition(clientDto.getPosition());
            allClientDto.setNationality(clientDto.getNationality());
            allClientDto.setOrganization(clientDto.getOrganization());
            allClientDto.setCurrentResidentStatus(clientDto.getCurrentResidentStatus());
            allClientDto.setHowHasThePersonHeardFromUs(clientDto.getHowHasThePersonHeardFromUs());
            allClientDto.setVulnerableWhenAssertingRights(clientDto.getVulnerableWhenAssertingRights());
            allClientDto.setInterpreterNecessary(clientDto.getInterpreterNecessary());
            allClientDto.setMaritalStatus(clientDto.getMaritalStatus());
            allClientDto.setLabourMarketAccess(clientDto.getLabourMarketAccess());
            allClientDto.setSocialInsuranceNumber(clientDto.getSocialInsuranceNumber());

            allClientDtoList.add(allClientDto);
        }

        return allClientDtoList;
    }

}
