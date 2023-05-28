package com.example.shoeshop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Integer id;

    @Column(name = "brand_name", nullable = false, unique = true, length = 128)
    private String name;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    private List<Shoes> shoes;

    public Brand(String name){
        this.name = name;
    }

}
