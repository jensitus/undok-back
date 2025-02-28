package at.undok.decrypt;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.entity.Address;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.repository.AddressRepo;
import at.undok.undok.client.repository.PersonRepo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class KeyService {

    private final AddressRepo addressRepo;
    private final PersonRepo personRepo;
    private final AttributeEncryptor attributeEncryptor;
    @Value("${undok.key}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public KeyService(AddressRepo addressRepo, PersonRepo personRepo, AttributeEncryptor attributeEncryptor) {
        this.addressRepo = addressRepo;
        this.personRepo = personRepo;
        this.attributeEncryptor = attributeEncryptor;
    }

    public void getAddresses() {
        List<Address> addresses = addressRepo.findAll();
        for (Address address : addresses) {
            decryptAddress(address.getId());
        }
        getPeople();
    }

    public void getPeople() {
        Iterable<Person> people = personRepo.findAll();
        for (Person person : people) {
            decryptPerson(person.getId());
        }
    }

    private void decryptAddress(UUID addressId) {
        Address address = addressRepo.findById(addressId).orElseThrow();
        log.info("encrypted address: {}", address);
        if (address.getCity() != null) {
            address.setCity(attributeEncryptor.convertToEntityAttribute(address.getCity()));
        }
        if (address.getCountry() != null) {
            address.setCountry(attributeEncryptor.convertToEntityAttribute(address.getCountry()));
        }
        if (address.getStreet() != null) {
            address.setStreet(attributeEncryptor.convertToEntityAttribute(address.getStreet()));
        }
        if (address.getZipCode() != null) {
            address.setZipCode(attributeEncryptor.convertToEntityAttribute(address.getZipCode()));
        }
        log.info("Decrypted address: {}", address);
        addressRepo.save(address);
    }

    private void decryptPerson(UUID personId) {
        Person person = personRepo.findById(personId).orElseThrow();
        if (person.getFirstName() != null) {
            person.setFirstName(attributeEncryptor.convertToEntityAttribute(person.getFirstName()));
        }
        if (person.getLastName() != null) {
            person.setLastName(attributeEncryptor.convertToEntityAttribute(person.getLastName()));
        }
        if (person.getEmail() != null) {
            person.setEmail(attributeEncryptor.convertToEntityAttribute(person.getEmail()));
        }
        if (person.getGender() != null) {
            person.setGender(attributeEncryptor.convertToEntityAttribute(person.getGender()));
        }
        if (person.getTelephone() != null) {
            person.setTelephone(attributeEncryptor.convertToEntityAttribute(person.getTelephone()));
        }
        personRepo.save(person);
    }
}
