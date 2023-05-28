package com.example.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Person user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shoes_id", nullable = false)
    private Shoes shoes;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

}
