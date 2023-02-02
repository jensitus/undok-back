package at.undok.undok.client.service;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.repository.PersonRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrateToDecryptedService {

    private final PersonRepo personRepo;

    private final AttributeEncryptor attributeEncryptor;

    public void migratePersons() {
        Iterable<Person> all = personRepo.findAll();
        for (Person person : all) {

            if (person == null) {
                return;
            }
            if (person.getFirstName() != null) {
                person.setFirstName(attributeEncryptor.convertToEntityAttribute(person.getFirstName()));
            }
            if (person.getLastName() != null) {
                person.setLastName(attributeEncryptor.convertToEntityAttribute(person.getLastName()));
            }
            if (person.getEmail() != null) {
                person.setEmail(attributeEncryptor.convertToEntityAttribute(person.getEmail()));
            }
            if (person.getTelephone() != null) {
                person.setTelephone(attributeEncryptor.convertToEntityAttribute(person.getTelephone()));
            }
            if (person.getGender() != null) {
                person.setGender(attributeEncryptor.convertToEntityAttribute(person.getGender()));
            }
            log.info(person.toString());
        };
    }

}
