package com.example.gamify.controllers;

import com.example.gamify.models.dtos.absolute.TasksDTO;
import com.example.gamify.models.dtos.absolute.ProfileDTO;
import com.example.gamify.models.dtos.input.ItemInputDTO;
import com.example.gamify.models.dtos.input.ProfileInputDTO;
import com.example.gamify.models.dtos.output.ItemOutputDTO;
import com.example.gamify.models.dtos.output.StoreOutputDTO;
import com.example.gamify.services.ProfileQueryService;
import com.example.gamify.services.ProfileService;
import com.example.gamify.services.StoreItemService;
import com.example.gamify.utils.exceptions.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
@Validated
@Tag(name = "Profiles", description = "Endpoints relacionados a perfis")
public class ProfileController {
    private final ProfileService profileService;
    private final ProfileQueryService profileQueryService;
    private final StoreItemService storeItemService;

    @GetMapping("/by-user-email/{email}")
    @Operation(summary = "Obter perfil por email do usuário", description = "Retorna o perfil do usuário com base no email fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<ProfileDTO> getProfileByUserEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(profileQueryService.getProfileByEmail(email));
        } catch (ProfileNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/by-user-uuid/{uuid}")
    @Operation(summary = "Obter perfil por UUID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<ProfileDTO> getProfileByUserUuid(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok(profileQueryService.getProfileByUserId(uuid));
        } catch (ProfileNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/{uuid}/my_tasks")
    @Operation(summary = "Obter tarefas do perfil por UUID", description = "Retorna as tarefas associadas ao perfil do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas obtidas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<List<TasksDTO>> getMyTasks(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok(profileQueryService.getProfileTasks(uuid));
        } catch (ProfileNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PutMapping("/update/{uuid}")
    @Operation(summary = "Atualizar informações do perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<?> updateUserInfo(@RequestBody @Valid ProfileInputDTO profileDTO, @PathVariable UUID uuid) {
        try {
            profileService.updateProfileInfo(profileDTO, uuid);
            return ResponseEntity.ok().build();
        } catch (ProfileNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/{uuid}/mystore")
    @Operation(summary = "Obter loja do perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loja obtida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil ou loja não encontrado")
    })
    public ResponseEntity<StoreOutputDTO> getMyStore(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok(profileQueryService.getStoreByProfileId(uuid));
        } catch (ProfileNotFoundException | StoreNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PostMapping("/{uuid}/addItem")
    @Operation(summary = "Adicionar novo item à loja")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil ou loja não encontrado")
    })
    public ResponseEntity<ItemOutputDTO> addItem(@RequestBody @Valid ItemInputDTO itemInputDTO, @PathVariable UUID uuid) {
        try {
            ItemOutputDTO result = storeItemService.addItemToStore(uuid, itemInputDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (ProfileNotFoundException | StoreNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PutMapping("/{profileId}/updateItem/{itemId}")
    @Operation(summary = "Atualizar item da loja")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil ou item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Item não pertence ao perfil")
    })
    public ResponseEntity<ItemOutputDTO> updateItem(@RequestBody @Valid ItemInputDTO itemInputDTO,
                                                     @PathVariable UUID profileId, @PathVariable UUID itemId) {
        try {
            return ResponseEntity.ok(storeItemService.updateStoreItem(profileId, itemId, itemInputDTO));
        } catch (ProfileNotFoundException | ItemNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ItemNotOwnedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @DeleteMapping("/{profileId}/deleteItem/{itemId}")
    @Operation(summary = "Deletar item da loja")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil ou item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Item não pertence ao perfil"),
            @ApiResponse(responseCode = "409", description = "Item já foi comprado")
    })
    public ResponseEntity<?> deleteItem(@PathVariable UUID profileId, @PathVariable UUID itemId) {
        try {
            storeItemService.deleteStoreItem(profileId, itemId);
            return ResponseEntity.noContent().build();
        } catch (ProfileNotFoundException | ItemNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ItemNotOwnedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (ItemPurchasedException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @PutMapping("/{profileId}/purchaseItem/{itemId}")
    @Operation(summary = "Comprar item da loja")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item comprado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil ou item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Item não pertence ao perfil"),
            @ApiResponse(responseCode = "409", description = "Ouro insuficiente")
    })
    public ResponseEntity<ItemOutputDTO> purchaseItem(@PathVariable UUID profileId, @PathVariable UUID itemId) {
        try {
            return ResponseEntity.ok(storeItemService.purchaseStoreItem(profileId, itemId));
        } catch (ProfileNotFoundException | ItemNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ItemNotOwnedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (InsufficientGoldException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }
}
