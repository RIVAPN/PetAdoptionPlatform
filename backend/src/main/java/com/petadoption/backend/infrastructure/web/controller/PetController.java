package com.petadoption.backend.infrastructure.web.controller;

import com.petadoption.backend.core.domain.Pet;
import com.petadoption.backend.core.domain.PetService;
import com.petadoption.backend.core.domain.PetStatus;
import com.petadoption.backend.infrastructure.web.dto.CreatePetRequest;
import com.petadoption.backend.infrastructure.web.dto.PetResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<PetResponse> create(@RequestBody CreatePetRequest request,
                                              Authentication authentication) {

        // e-mail (ou username) do usuário autenticado — vem do token JWT
        String email = authentication.getName();

        Pet pet = petService.createPet(request, email);
        PetResponse response = toResponse(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lista somente por status (por padrão AVAILABLE)
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