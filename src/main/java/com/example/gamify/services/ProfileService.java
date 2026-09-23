package com.example.gamify.services;

import com.example.gamify.models.dtos.input.ProfileInputDTO;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.utils.exceptions.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    public boolean validateProfile(UUID profileId) {
        return profileRepository.findByUserId(profileId).isPresent();
    }

    @Transactional
    public void updateProfileInfo(@NonNull ProfileInputDTO profileDTO, UUID userId) throws ProfileNotFoundException {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Não há perfil para o usuário com UUID: " + userId)
                );
        profile.setName(profileDTO.name());
        profile.setAvatarUrl(profileDTO.avatarUrl());
        profileRepository.save(profile);
    }
}

