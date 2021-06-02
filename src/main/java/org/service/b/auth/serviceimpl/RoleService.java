package org.service.b.auth.serviceimpl;

import org.service.b.auth.model.entity.Role;
import org.service.b.auth.model.entity.RoleName;
import org.service.b.auth.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepo roleRepo;

    public Role getAdminRole() {
        return roleRepo.findByName(RoleName.ADMIN).orElseThrow(() -> new RuntimeException("The Fucking Role couldn't be found, sorry"));
    }

    public Role getUserRole() {
        return roleRepo.findByName(RoleName.USER).orElseThrow(() -> new RuntimeException("No Role Today my Love is gone away"));
    }

    public Role getConfirmedRole() {
        return roleRepo.findByName(RoleName.CONFIRMED).orElseThrow(() -> new RuntimeException("Tja"));
    }

}
