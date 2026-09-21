package com.example.gamify.controllers;

import com.example.gamify.models.dtos.absolute.TasksDTO;
import com.example.gamify.models.dtos.input.ProfileInputDTO;
import com.example.gamify.services.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
@Validated
@Tag(name = "Profiles", description = "Endpoints relacionados a perfis")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/by-user-email/{email}")
    @Operation(summary = "Obter perfil por email do usuário", description = "Retorna o perfil do usuário com base no email fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email do usuário inválido"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public Object getProfileByUserEmail(@PathVariable("email") String userEmail) {
        return profileService.getProfileByUserEmail(userEmail);
    }

    @GetMapping("/by-user-uuid/{uuid}")
    public Object getProfileByUserUuid(@PathVariable("uuid") String userUuid) {
        return profileService.getProfileByUserUuid(UUID.fromString(userUuid));
    }

    @GetMapping("/{uuid}/my_tasks")
    @Operation(summary = "Obter tarefas do perfil por UUID", description = "Retorna as tarefas associadas ao perfil do usuário com base no UUID fornecido.")
    public List<TasksDTO> getMyTasks(@PathVariable("uuid") String userUuid) {
        return profileService.getMyTasks(UUID.fromString(userUuid));
    }

    @PutMapping(    "/update/{uuid}")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
                    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
            }
    )
    public String updateUserInfo(@RequestBody @Valid ProfileInputDTO profileDTO, @PathVariable("uuid") String userUuid) {
        return profileService.updateProfileInfo(profileDTO, UUID.fromString(userUuid)) ? "Success" : "Failure";
    }
}
