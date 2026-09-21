package com.example.gamify.services;

import com.example.gamify.models.dtos.absolute.ProfileDTO;
import com.example.gamify.models.dtos.absolute.TasksDTO;
import com.example.gamify.models.dtos.input.ProfileInputDTO;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Tasks;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.utils.exceptions.ProfileNotExists;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    final ProfileRepository profileRepository;

    private void updateProfileInto(@NonNull ProfileInputDTO profileDTO, UUID userId){
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new ProfileNotExists("Não há perfil para o usuário com UUID: " + userId)
                );
        profile.setName(profileDTO.name());
        profile.setAvatarUrl(profileDTO.avatarUrl());
        profileRepository.save(profile);
    }

    @Validated
    public boolean updateProfileInfo(ProfileInputDTO profileDTO, UUID userId) {
        try {
            updateProfileInto(profileDTO, userId);
        } catch (ProfileNotExists e) {
            return false;
        }
        return true;
    }

    public ProfileDTO getProfileByUserEmail(String userEmail) {
        Profile profile = profileRepository.findByEmail(userEmail)
                .orElseThrow(
                        () -> new ProfileNotExists("Não há perfil para o usuário com email: " + userEmail)
                );
        return profile.toDTO();
    }

    public Object getProfileByUserUuid(UUID userUuid) {
        Profile profile = profileRepository.findByUserId(userUuid)
                .orElseThrow(
                        () -> new ProfileNotExists("Não há perfil para o usuário com UUID: " + userUuid)
                );
        return profile.toDTO();
    }

    public List<TasksDTO> getMyTasks(UUID uuid) {
        Profile profile = profileRepository.findByUserId(uuid)
                .orElseThrow(
                        () -> new ProfileNotExists("Não há perfil para o usuário com UUID: " + uuid)
                );
        return profile.getTasks().stream()
                .map(Tasks::toDTO)
                .toList();
    }
}
