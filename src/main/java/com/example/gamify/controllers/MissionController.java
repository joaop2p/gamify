package com.example.gamify.controllers;

import com.example.gamify.models.dtos.input.MissionInputDTO;
import com.example.gamify.models.dtos.output.MissionOutPutDTO;
import com.example.gamify.services.MissionService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return missionService.createNewMission(missionInputDTO);
    }
}
