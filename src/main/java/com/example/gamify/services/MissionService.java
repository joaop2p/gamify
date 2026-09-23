package com.example.gamify.services;

import com.example.gamify.models.dtos.input.MissionInputDTO;
import com.example.gamify.models.dtos.output.MissionOutPutDTO;
import com.example.gamify.models.entities.Mission;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Tasks;
import com.example.gamify.repository.MissionRespository;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.repository.TasksRepository;
import com.example.gamify.utils.exceptions.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRespository missionRespository;
    private final ProfileRepository profileRepository;
    private final TasksRepository tasksRepository;

    public MissionOutPutDTO createNewMission(MissionInputDTO missionInputDTO){
        Profile profile = profileRepository.findById(missionInputDTO.userId())
                .orElseThrow(() -> new ProfileNotFoundException("Profile with id " + missionInputDTO.userId() + " does not exist"));
        Optional<Tasks> task = Optional.empty();
        if (missionInputDTO.taskId().isPresent()) {
            task = tasksRepository.findById(missionInputDTO.taskId().get());
            if (task.isEmpty()) {
                throw new RuntimeException("Task with id " + missionInputDTO.taskId() + " does not exist");
            }
        }
        Mission newMission = Mission.fromMissionInputDTO(missionInputDTO, profile, task);
        missionRespository.save(newMission);
        return newMission.toMissionOutPutDTO();
    }
}
