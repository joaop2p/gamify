package com.example.gamify.repository;

import com.example.gamify.models.entities.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRespository extends JpaRepository<Mission, Integer> {
}
