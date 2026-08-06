package com.diogorocha.uol_players.entity;

import com.diogorocha.uol_players.enums.CodenameGroup;
import jakarta.persistence.*;

@Entity
@Table(
        name = "players",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_player_codename_group",
                        columnNames = {"codename", "codename_group"}
                )
        }
)

public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String codename;

    @Enumerated(EnumType.STRING)
    @Column(name = "codename_group", nullable = false)
    private CodenameGroup codenameGroup;

    protected Player(){
    }

    public Player(
            String name,
            String email,
            String phone,
            String codename,
            CodenameGroup codenameGroup
    ){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.codename = codename;
        this.codenameGroup = codenameGroup;
    }

    public Long getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCodename() {
        return codename;
    }

    public CodenameGroup getCodenameGroup() {
        return codenameGroup;
    }
}
