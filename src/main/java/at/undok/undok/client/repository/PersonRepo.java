package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Repository
public interface PersonRepo extends PagingAndSortingRepository<Person, UUID> {

    Person findByFirstName(String firstName);

}
