package com.example.gamify.controllers;


import com.example.gamify.models.dtos.input.TasksInputDTO;
import com.example.gamify.models.dtos.output.TasksOutPutDTO;
import com.example.gamify.services.TasksService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
