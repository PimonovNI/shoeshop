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
@Table(name = "shoes")
public class Shoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shoes_id")
    private Long id;

    @Column(name = "shoes_name", nullable = false, unique = true, length = 128)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_name", nullable = false, unique = true, length = 128)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 16)
    private Gender gender;

    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @OneToMany(mappedBy = "shoes", fetch = FetchType.LAZY)
    private List<Availability> availabilities;

    @Builder.Default
    @OneToMany(mappedBy = "shoes", fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();

}
