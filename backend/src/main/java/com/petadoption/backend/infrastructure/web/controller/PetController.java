package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.Pet;
import com.petadoption.backend.core.domain.PetService;
import com.petadoption.backend.core.domain.PetStatus;
import com.petadoption.backend.infrastructure.web.dto.CreatePetRequest;
import com.petadoption.backend.infrastructure.web.dto.PetResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // ------------ C R E A T E ------------

    @PostMapping
    public ResponseEntity<PetResponse> create(@RequestBody CreatePetRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedEmail = userDetails.getUsername();
        Pet pet = petService.createPet(request, authenticatedEmail);
        PetResponse response = toResponse(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ------------ U P D A T E ------------

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> update(@PathVariable Long id,
                                              @RequestBody CreatePetRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedEmail = userDetails.getUsername();
        Pet pet = petService.updatePet(id, request, authenticatedEmail);
        PetResponse response = toResponse(pet);
        return ResponseEntity.ok(response);
    }

    // ------------ D E L E T E ------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedEmail = userDetails.getUsername();
        petService.deletePet(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    // ------------ R E A D  ------------

    // catálogo público (GET /api/pets?status=AVAILABLE)
    @GetMapping
    public List<PetResponse> list(@RequestParam(required = false) String status) {
        PetStatus st = (status == null || status.isBlank())
                ? PetStatus.AVAILABLE
                : PetStatus.valueOf(status.toUpperCase());

        return petService.listByStatus(st).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PetResponse getById(@PathVariable Long id) {
        return toResponse(petService.getById(id));
    }

    // ------------ M A P P E R ------------

    private PetResponse toResponse(Pet pet) {
        String ownerType;
        Long ownerId;

        if (pet.getOwnerUser() != null) {
            ownerType = "USER";
            ownerId = pet.getOwnerUser().getId();
        } else if (pet.getOwnerOrg() != null) {
            ownerType = "ORG";
            ownerId = pet.getOwnerOrg().getId();
        } else {
            ownerType = "UNKNOWN";
            ownerId = null;
        }

        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getSize(),
                pet.getStatus(),
                ownerType,
                ownerId
        );
    }
}