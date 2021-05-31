package org.service.b.auth.repository;

import org.service.b.auth.model.entity.Role;
import org.service.b.auth.model.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Integer> {

  Optional<Role> findByName(RoleName name);

}
