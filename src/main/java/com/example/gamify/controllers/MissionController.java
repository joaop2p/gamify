package com.example.gamify.controllers;

import com.example.gamify.models.dtos.input.MissionInputDTO;
import com.example.gamify.models.dtos.output.MissionOutPutDTO;
import com.example.gamify.services.MissionService;
import com.example.gamify.utils.exceptions.MissionAlreadyCompleted;
import com.example.gamify.utils.exceptions.MissionNotFoundException;
import com.example.gamify.utils.exceptions.ProfileNotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("missions")
@RestController
@Tag(name = "Missions", description = "Endpoints relacionados a missões")
public class MissionController {
    private final MissionService missionService;

    @PostMapping("/new")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Missão criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados da missão inválidos"),
                    @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
            }
    )
    public MissionOutPutDTO createNewMission(@RequestBody MissionInputDTO missionInputDTO){
        try {
            return ResponseEntity.ok(missionService.createNewMission(missionInputDTO)).getBody();
        } catch (ProfileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping("/complete/{missionId}")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Missão completada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Missão já completada"),
                    @ApiResponse(responseCode = "404", description = "Missão não encontrada")
            }
    )
    public MissionOutPutDTO completeMission(@PathVariable UUID missionId){
        try {
            return ResponseEntity.ok(missionService.completeMission(missionId)).getBody();
        } catch (MissionNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (MissionAlreadyCompleted e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
