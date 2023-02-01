package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Fully;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FullyRepo extends ReadOnlyRepo<Fully, UUID> {


}
