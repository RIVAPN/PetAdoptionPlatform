package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.RoleJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreateUserRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserJpaRepository userRepository,
                       RoleJpaRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        // senha com hash
        String hashed = passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(hashed);

        // tenta adicionar ROLE_ADOPTER, se existir
        roleRepository.findByName("ROLE_ADOPTER").ifPresent(user::addRole);

        return userRepository.save(user);
    }


    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
}