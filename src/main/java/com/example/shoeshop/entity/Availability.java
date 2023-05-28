package com.example.shoeshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "availability")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Long id;

    @Column(name = "count", nullable = false)
    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shoes_id", nullable = false)
    private Shoes shoes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    public Availability(Size size, Integer count){
        this.size = size;
        this.count = count;
    }

    public Availability(Shoes shoes, Size size, Integer count){
        this.shoes = shoes;
        this.size = size;
        this.count = count;
    }

}
