package dev.arif.userservice.service;

import dev.arif.userservice.models.Role;
import dev.arif.userservice.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) { this.roleRepository = roleRepository; }

    public Role createRole(String name) {
        Role role = new Role();
        role.setRole(name);

        return roleRepository.save(role);
    }
}
