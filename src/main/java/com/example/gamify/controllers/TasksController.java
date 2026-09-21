package com.example.gamify.controllers;


import com.example.gamify.models.dtos.absolute.ProfileDTO;
import com.example.gamify.models.dtos.input.TaskCompleteInputDTO;
import com.example.gamify.models.dtos.input.TasksInputDTO;
import com.example.gamify.models.dtos.output.TasksOutPutDTO;
import com.example.gamify.services.TasksService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/tasks")
@RequiredArgsConstructor
@RestController
@Tag(name = "Tasks", description = "Endpoints relacionados a tarefas")
@Validated
public class TasksController {
    private final TasksService tasksService;

    @PostMapping("/newtask")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da tarefa inválidos"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public TasksOutPutDTO createNewTask(@Valid @RequestBody TasksInputDTO taskInputDTO) {
        return tasksService.createTask(taskInputDTO);
    }
    @PostMapping("/complete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da tarefa inválidos"),
            @ApiResponse(responseCode = "404", description = "Perfil ou tarefa não encontrado")
    })
    public ProfileDTO completeTask(@Valid @RequestBody TaskCompleteInputDTO request) {
        return tasksService.completedTask(request.taskId());
    }

    @PutMapping("/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da tarefa inválidos"),
            @ApiResponse(responseCode = "404", description = "Perfil ou tarefa não encontrado")
    })
    public TasksOutPutDTO updateTask(@Valid @RequestBody TasksInputDTO taskInputDTO, @RequestParam UUID taskId) {
        return tasksService.updateTask(taskInputDTO, taskId);
    }

}
