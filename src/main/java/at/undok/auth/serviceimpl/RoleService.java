package at.undok.auth.serviceimpl;

import at.undok.auth.model.entity.Role;
import at.undok.auth.model.entity.RoleName;
import at.undok.auth.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepo roleRepo;

    public Role getAdminRole() {
        return roleRepo.findByName(RoleName.ROLE_ADMIN)
                       .orElseThrow(
                               () -> new RuntimeException("The Fucking Role couldn't be found, sorry")
                       );
    }

    public Role getUserRole() {
        return roleRepo.findByName(RoleName.ROLE_USER)
                       .orElseThrow(
                               () -> new RuntimeException("No Role Today my Love is gone away")
                       );
    }

    public Role getConfirmedRole() {
        return roleRepo.findByName(RoleName.ROLE_CONFIRMED)
                       .orElseThrow(
                               () -> new RuntimeException("Tja")
                       );
    }

    public Role getLockedRole() {
        return roleRepo.findByName(RoleName.ROLE_LOCKED)
                       .orElseThrow(
                               () -> new RuntimeException("No Role found")
                       );
    }

}
