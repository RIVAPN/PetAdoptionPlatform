package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.RoleJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreateUserRequest;
import com.petadoption.backend.infrastructure.web.dto.UpdateUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserJpaRepository userRepository,
                       RoleJpaRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CRIAÇÃO
    // =========================
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

    // =========================
    // BUSCAS
    // =========================
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    // =========================
    // OPERAÇÕES DO PRÓPRIO USUÁRIO
    // =========================
    @Transactional
    public User updateSelf(String email, UpdateUserRequest request) {
        User user = getByEmail(email);

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteSelf(String email) {
        User user = getByEmail(email);
        userRepository.delete(user);
    }
}