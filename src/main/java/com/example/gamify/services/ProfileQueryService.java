package com.example.gamify.services;

import com.example.gamify.models.dtos.absolute.ProfileDTO;
import com.example.gamify.models.dtos.absolute.TasksDTO;
import com.example.gamify.models.dtos.output.StoreOutputDTO;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Store;
import com.example.gamify.models.entities.Tasks;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.repository.StoreRepository;
import com.example.gamify.utils.exceptions.ProfileNotFoundException;
import com.example.gamify.utils.exceptions.StoreNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileQueryService {
    private final ProfileRepository profileRepository;
    private final StoreRepository storeRepository;

    public ProfileDTO getProfileByEmail(String email) throws ProfileNotFoundException {
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Não há perfil para o usuário com email: " + email)
                );
        return profile.toDTO();
    }

    public ProfileDTO getProfileByUserId(UUID userId) throws ProfileNotFoundException {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Não há perfil para o usuário com UUID: " + userId)
                );
        return profile.toDTO();
    }

    public List<TasksDTO> getProfileTasks(UUID userId) throws ProfileNotFoundException {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Não há perfil para o usuário com UUID: " + userId)
                );
        return profile.getTasks().stream()
                .map(Tasks::toDTO)
                .toList();
    }

    public StoreOutputDTO getStoreByProfileId(UUID profileId) throws ProfileNotFoundException, StoreNotFoundException {
        Profile profile = profileRepository.findByUserId(profileId)
                .orElseThrow(
                        () -> new ProfileNotFoundException("Não há perfil para o usuário com UUID: " + profileId)
                );
        Store store = storeRepository.findByProfile_UserId(profileId)
                .orElseThrow(() -> new StoreNotFoundException("Loja não encontrada para o perfil: " + profileId));
        return store.toOutputDTO();
    }
}
