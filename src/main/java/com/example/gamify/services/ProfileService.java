package com.example.gamify.services;

import com.example.gamify.models.dtos.absolute.ProfileDTO;
import com.example.gamify.models.dtos.absolute.TasksDTO;
import com.example.gamify.models.dtos.output.TasksOutPutDTO;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Tasks;
import com.example.gamify.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    final ProfileRepository profileRepository;

    public ProfileDTO getProfileByUserEmail(String userEmail) {
        Profile profile = profileRepository.findByEmail(userEmail)
                .orElseThrow();
        return profile.toDTO();
    }

    public Object getProfileByUserUuid(UUID userUuid) {
        Profile profile = profileRepository.findByUserId(userUuid)
                .orElseThrow();
        return profile.toDTO();
    }

    public List<TasksDTO> getMyTasks(UUID uuid) {
        Profile profile = profileRepository.findByUserId(uuid)
                .orElseThrow();
        return profile.getTasks().stream()
                .map(Tasks::toDTO)
                .toList();
    }
}
