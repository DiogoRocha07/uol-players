package com.diogorocha.uol_players.repository;

import com.diogorocha.uol_players.entity.Player;
import com.diogorocha.uol_players.enums.CodenameGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findAllByCodenameGroup(CodenameGroup codenameGroup);
}