package com.example.shoeshop.repository.impl;

import com.example.shoeshop.entity.Cart;
import com.example.shoeshop.entity.Shoes;
import com.example.shoeshop.repository.CustomCartRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CustomCartRepositoryImpl implements CustomCartRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public CustomCartRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<Tuple> findByUserIdWithUserAndShoes(Long userId) {
        CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = cb.createQuery(Tuple.class);

        Root<Shoes> shoes = criteria.from(Shoes.class);
        var cart = shoes.join("carts", JoinType.INNER);
        var user = cart.join("user", JoinType.INNER);
        shoes.fetch("brand", JoinType.INNER);

        criteria.multiselect(shoes, cart.get("size").get("id"))
                .where(cb.equal(user.get("id"), userId));

        return entityManagerFactory.createEntityManager()
                .createQuery(criteria)
                .getResultList();
    }

    @Override
    public List<Cart> findByUserIdWithShoesAndAvailability(Long userId) {
        CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Cart> criteria = cb.createQuery(Cart.class);

        Root<Cart> cart = criteria.from(Cart.class);
        var shoes = cart.fetch("shoes", JoinType.INNER);
        shoes.fetch("availabilities", JoinType.INNER);

        criteria.select(cart)
                .where(cb.equal(cart.get("user").get("id"), userId));

        return entityManagerFactory.createEntityManager()
                .createQuery(criteria)
                .getResultList();
    }
}
