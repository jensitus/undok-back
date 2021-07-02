package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepo extends JpaRepository<Address, UUID> {



}
