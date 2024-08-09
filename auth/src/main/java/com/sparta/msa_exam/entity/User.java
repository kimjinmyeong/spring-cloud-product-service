package com.sparta.msa_exam.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "\"user\"")
@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    public User (String userId) {
        this.userId = userId;
    }

    public User() {

    }
}
