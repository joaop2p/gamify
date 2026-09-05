package com.example.gamify.services;

import com.example.gamify.models.dtos.input.TasksInputDTO;
import com.example.gamify.models.dtos.output.TasksOutPutDTO;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Tasks;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.repository.TasksRepository;
import com.example.gamify.utils.exceptions.ProfileNotExists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TasksService {
    private final TasksRepository tasksRepository;
    private final ProfileRepository profileRepository;

    public TasksOutPutDTO createTask(TasksInputDTO taskInputDTO) {
        Profile profile = profileRepository.findByUserId(taskInputDTO.profileUuid())
                .orElseThrow(
                        () -> new ProfileNotExists("Profile not found with id: " + taskInputDTO.profileUuid())
                );
        Tasks tasks = Tasks.fromInputDTO(taskInputDTO, profile);
        return tasksRepository.save(tasks).toOutputDTO();
    }
}
