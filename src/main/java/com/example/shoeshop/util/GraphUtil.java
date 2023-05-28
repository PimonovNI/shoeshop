package com.example.shoeshop.util;

import com.example.shoeshop.entity.Availability;
import com.example.shoeshop.entity.Shoes;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;

public class GraphUtil {
    public static EntityGraph<Shoes> shoesWithBrandAndSize(EntityManager entityManager){
        EntityGraph<Shoes> graph = entityManager.createEntityGraph(Shoes.class);
        graph.addAttributeNodes("brand", "availabilities");
        Subgraph<Availability> availabilities = graph.addSubgraph("availabilities", Availability.class);
        availabilities.addAttributeNodes("size");
        return graph;
    }

    public static EntityGraph<Shoes> shoesWithAvailability(EntityManager entityManager) {
        EntityGraph<Shoes> graph = entityManager.createEntityGraph(Shoes.class);
        graph.addAttributeNodes("availabilities");
        return graph;
    }
}
