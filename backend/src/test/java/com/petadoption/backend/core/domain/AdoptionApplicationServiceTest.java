package com.petadoption.backend.core.domain;

import com.petadoption.backend.infrastructure.persistence.jpa.AdoptionApplicationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.OrganizationJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.PetJpaRepository;
import com.petadoption.backend.infrastructure.persistence.jpa.UserJpaRepository;
import com.petadoption.backend.infrastructure.web.dto.CreateAdoptionApplicationRequest;
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
class AdoptionApplicationServiceTest {

    @Mock
    private AdoptionApplicationJpaRepository applicationRepository;

    @Mock
    private PetJpaRepository petRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private OrganizationJpaRepository organizationRepository;

    @InjectMocks
    private AdoptionApplicationService applicationService;

    @Test
    void createApplication_deveLancarExcecaoQuandoNaoHaAdotante() {
        // arrange
        CreateAdoptionApplicationRequest request = new CreateAdoptionApplicationRequest();
        request.setPetId(1L);
        // nem adopterUserId nem adopterOrgId

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.createApplication(request)
        );

        assertEquals("É necessário informar adopterUserId ou adopterOrgId.", ex.getMessage());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void createApplication_deveLancarExcecaoQuandoHaDoisAdotantes() {
        // arrange
        CreateAdoptionApplicationRequest request = new CreateAdoptionApplicationRequest();
        request.setPetId(1L);
        request.setAdopterUserId(10L);
        request.setAdopterOrgId(20L); // dois adotantes

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.createApplication(request)
        );

        assertEquals("Informe apenas adopterUserId ou adopterOrgId, não ambos.", ex.getMessage());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void createApplication_deveCriarAplicacaoPendenteComAdotanteUsuario() {
        // arrange
        CreateAdoptionApplicationRequest request = new CreateAdoptionApplicationRequest();
        request.setPetId(1L);
        request.setAdopterUserId(10L);

        Pet pet = new Pet();
        pet.setId(1L);

        User adopter = new User();
        adopter.setId(10L);
        adopter.setName("Kamilla");

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(userRepository.findById(10L)).thenReturn(Optional.of(adopter));
        when(applicationRepository.save(any(AdoptionApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // act
        AdoptionApplication app = applicationService.createApplication(request);

        // assert
        assertEquals(ApplicationStatus.PENDING, app.getStatus());
        assertEquals(0, app.getCompatibilityScore());
        assertFalse(app.getHasBlockingFactor());
        assertNotNull(app.getPet());
        assertEquals(1L, app.getPet().getId());
        assertNotNull(app.getAdopterUser());
        assertEquals(10L, app.getAdopterUser().getId());
        assertNull(app.getAdopterOrg());
    }

    @Test
    void listByPet_deveDelegarAoRepositorio() {
        // arrange
        AdoptionApplication a1 = new AdoptionApplication();
        AdoptionApplication a2 = new AdoptionApplication();
        List<AdoptionApplication> apps = List.of(a1, a2);

        when(applicationRepository.findByPet_Id(1L)).thenReturn(apps);

        // act
        List<AdoptionApplication> result = applicationService.listByPet(1L);

        // assert
        assertSame(apps, result);
        verify(applicationRepository).findByPet_Id(1L);
    }
}