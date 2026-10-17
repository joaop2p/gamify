package com.example.gamify.services;

import com.example.gamify.models.dtos.input.MissionInputDTO;
import com.example.gamify.models.dtos.output.MissionOutPutDTO;
import com.example.gamify.models.entities.Mission;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Tasks;
import com.example.gamify.repository.MissionRepository;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.repository.TasksRepository;
import com.example.gamify.utils.exceptions.MissionAlreadyCompleted;
import com.example.gamify.utils.exceptions.MissionNotFoundException;
import com.example.gamify.utils.exceptions.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ProfileRepository profileRepository;
    private final TasksRepository tasksRepository;

    public MissionOutPutDTO createNewMission(MissionInputDTO missionInputDTO) throws RuntimeException {
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
        missionRepository.save(newMission);
        return newMission.toMissionOutPutDTO();
    }

    public MissionOutPutDTO completeMission(UUID missionId) throws MissionNotFoundException, MissionAlreadyCompleted {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException("Missão com ID " + missionId + " não encontrada"));

        if (mission.isFinished()) {
            throw new MissionAlreadyCompleted("A missão já foi finalizada em " + mission.getFinishedAt());
        }
        Profile profile = mission.getProfile();
        profile.setGold(profile.getGold() + mission.getReward());
        profile.setExperience(profile.getExperience() + mission.getExperience());
        profileRepository.save(profile);
        mission.setFinished(true);
        mission.setFinishedAt(LocalDateTime.now());
        missionRepository.save(mission);
        return mission.toMissionOutPutDTO();
    }
}
