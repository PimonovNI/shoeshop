package com.example.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 128)
    private String username;

    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 16)
    private Role role;

    @Column(name = "activation_code", length = 128)
    private String activationCode;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();

}
