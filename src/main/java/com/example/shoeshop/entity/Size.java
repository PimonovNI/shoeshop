package com.example.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "size")
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Integer id;

    @Column(name = "size_name", nullable = false, unique = true)
    private Integer size;

    @OneToMany(mappedBy = "size", fetch = FetchType.LAZY)
    private List<Availability> availabilities;

    @OneToMany(mappedBy = "size", fetch = FetchType.LAZY)
    private List<Cart> carts = new ArrayList<>();

    public Size(Integer size) {
        this.size = size;
    }

}
