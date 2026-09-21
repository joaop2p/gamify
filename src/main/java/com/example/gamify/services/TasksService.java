package com.example.gamify.services;

import com.example.gamify.models.dtos.absolute.ProfileDTO;
import com.example.gamify.models.dtos.input.TasksInputDTO;
import com.example.gamify.models.dtos.output.TasksOutPutDTO;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Tasks;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.repository.TasksRepository;
import com.example.gamify.utils.exceptions.ProfileNotExists;
import com.example.gamify.utils.exceptions.TaskAlreadyCompleted;
import com.example.gamify.utils.exceptions.TaskNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
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

    public TasksOutPutDTO updateTask(TasksInputDTO taskInputDTO, UUID taskId) {
        if (taskInputDTO.expirationDate() != null && taskInputDTO.expirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }
        Tasks tasks = tasksRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFound("Task not found with id: " + taskId));
        tasks.updateFromInputDTO(taskInputDTO);
        return tasksRepository.save(tasks).toOutputDTO();
    }

    public ProfileDTO completedTask(UUID taskId) {
        Tasks tasks = tasksRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFound("Task not found with id: " + taskId));

        if (tasks.isFinished()) {
            throw new TaskAlreadyCompleted("Task already completed: " + taskId);
        }

        Profile profile = tasks.getProfile();
        tasks.setFinished(true);
        profile.setGold(profile.getGold() + tasks.getReward());
        profile.getAttributes().updateAttribute(tasks.getAttribute(), tasks.getReward());

        tasksRepository.save(tasks);
        profileRepository.save(profile);

        log.info("Task {} completed by profile {}. Reward: {} gold",
                taskId, profile.getUserId(), tasks.getReward());

        return profile.toDTO();
    }
}
