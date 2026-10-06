package com.anastasia.adventurebook.repository;

import com.anastasia.adventurebook.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}