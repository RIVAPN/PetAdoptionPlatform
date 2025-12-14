package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.PetJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.RoleJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreatePetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetJpaRepository petRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private OrganizationJpaRepository organizationRepository;

    @Mock
    private RoleJpaRepository roleRepository;

    private PetService petService;

    @BeforeEach
    void setUp() {
        petService = new PetService(petRepository, userRepository, organizationRepository, roleRepository);
    }

    @Test
    void createPet_paraUsuarioAutenticadoDeveUsarEmailEPromoverTutor() {
        // arrange
        String email = "adopter@example.com";

        User owner = new User();
        owner.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(owner));

        Role tutorRole = new Role();
        tutorRole.setName("ROLE_TUTOR");
        when(roleRepository.findByName("ROLE_TUTOR")).thenReturn(Optional.of(tutorRole));

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet saved = invocation.getArgument(0);
            saved.setId(10L);         // aqui pode ter setId em Pet, que normalmente existe
            return saved;
        });

        CreatePetRequest request = new CreatePetRequest();
        request.setName("Luna");
        request.setSpecies("DOG");
        request.setSize("MEDIUM");
        request.setAgeYears(3);
        // sem ownerOrgId -> dono será o usuário autenticado

        // act
        Pet pet = petService.createPet(request, email);

        // assert
        assertEquals(owner, pet.getOwnerUser());
        assertNull(pet.getOwnerOrg());
        assertEquals(10L, pet.getId());

        verify(userRepository).findByEmail(email);
        verify(roleRepository).findByName("ROLE_TUTOR");
        // se no serviço você estiver salvando o usuário promovido:
        // verify(userRepository).save(owner);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void createPet_comOwnerOrgIdDeveUsarOrganizacao() {
        // arrange
        String email = "adopter@example.com"; // aqui não importa tanto

        Organization org = new Organization();
        org.setName("ONG Teste");

        when(organizationRepository.findById(5L)).thenReturn(Optional.of(org));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        CreatePetRequest request = new CreatePetRequest();
        request.setName("Mia");
        request.setSpecies("CAT");
        request.setSize("SMALL");
        request.setAgeYears(2);
        request.setOwnerOrgId(5L);

        // act
        Pet pet = petService.createPet(request, email);

        // assert
        assertEquals(org, pet.getOwnerOrg());
        assertNull(pet.getOwnerUser());
        assertEquals(20L, pet.getId());

        verify(organizationRepository).findById(5L);
        verify(petRepository).save(any(Pet.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void createPet_comUserEOrgDeveLancarIllegalArgumentException() {
        // arrange
        CreatePetRequest request = new CreatePetRequest();
        request.setName("Bob");
        request.setSpecies("DOG");
        request.setOwnerUserId(1L);
        request.setOwnerOrgId(2L);

        // act + assert
        assertThrows(IllegalArgumentException.class,
                () -> petService.createPet(request, "adopter@example.com"));

        verifyNoInteractions(petRepository);
    }
}