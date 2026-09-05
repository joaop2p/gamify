package com.example.gamify.repository;

import com.example.gamify.models.entities.Profile;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    public Optional<Profile> findByUserId(UUID userId);
    public Optional<Profile> findByEmail(String email);
}
