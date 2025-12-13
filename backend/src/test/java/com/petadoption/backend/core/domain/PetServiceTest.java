package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.PetJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreatePetRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetJpaRepository petRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private OrganizationJpaRepository organizationRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void createPet_deveLancarExcecaoQuandoNaoHaDono() {
        // arrange
        CreatePetRequest request = new CreatePetRequest();
        request.setName("Luna");
        request.setSpecies("DOG");
        // nem ownerUserId nem ownerOrgId são definidos

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> petService.createPet(request)
        );

        assertEquals("Pet deve possuir exatamente um tipo de dono: usuário OU organização.", ex.getMessage());
        verify(petRepository, never()).save(any());
    }

    @Test
    void createPet_deveLancarExcecaoQuandoHaDoisDonos() {
        // arrange
        CreatePetRequest request = new CreatePetRequest();
        request.setName("Luna");
        request.setSpecies("DOG");
        request.setOwnerUserId(1L);
        request.setOwnerOrgId(2L); // ambos preenchidos

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> petService.createPet(request)
        );

        assertEquals("Pet deve possuir exatamente um tipo de dono: usuário OU organização.", ex.getMessage());
        verify(petRepository, never()).save(any());
    }

    @Test
    void createPet_deveCriarPetComDonoUsuarioEStatusDefaultAvailable() {
        // arrange
        CreatePetRequest request = new CreatePetRequest();
        request.setName("Luna");
        request.setSpecies("DOG");
        request.setSize("MEDIUM");
        request.setAgeYears(2);
        request.setOwnerUserId(1L);   // dono é usuário
        // request.setStatus(null) -> deve virar AVAILABLE automaticamente

        User owner = new User();
        owner.setId(1L);
        owner.setName("Kamilla");

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        Pet created = petService.createPet(request);

        // assert
        assertEquals("Luna", created.getName());
        assertEquals("DOG", created.getSpecies());
        assertEquals(PetStatus.AVAILABLE, created.getStatus()); // default
        assertNotNull(created.getOwnerUser());
        assertEquals(1L, created.getOwnerUser().getId());
        assertNull(created.getOwnerOrg());
    }

    @Test
    void listByStatus_deveDelegarAoRepositorio() {
        // arrange
        Pet p1 = new Pet();
        Pet p2 = new Pet();
        List<Pet> pets = List.of(p1, p2);

        when(petRepository.findByStatus(PetStatus.AVAILABLE)).thenReturn(pets);

        // act
        List<Pet> result = petService.listByStatus(PetStatus.AVAILABLE);

        // assert
        assertSame(pets, result);
        verify(petRepository).findByStatus(PetStatus.AVAILABLE);
    }
}