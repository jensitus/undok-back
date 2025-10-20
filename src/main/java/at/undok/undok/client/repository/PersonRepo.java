package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepo extends JpaRepository<Person, UUID> {

    List<Person> findByFirstName(String firstName);

}
