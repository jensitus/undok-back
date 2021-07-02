package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Person;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PersonRepo extends PagingAndSortingRepository<Person, UUID> {

    Person findByFirstName(String firstName);

}
