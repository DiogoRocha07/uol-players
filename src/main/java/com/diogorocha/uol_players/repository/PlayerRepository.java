package com.diogorocha.uol_players.repository;

import com.diogorocha.uol_players.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerRepository extends JpaRepository<Player, Long> {
}