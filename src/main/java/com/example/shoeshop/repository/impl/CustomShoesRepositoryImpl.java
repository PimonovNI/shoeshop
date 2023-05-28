package com.example.shoeshop.repository.impl;

import com.example.shoeshop.entity.Gender;
import com.example.shoeshop.entity.Shoes;
import com.example.shoeshop.repository.CustomShoesRepository;
import com.example.shoeshop.util.GraphUtil;
import com.example.shoeshop.util.SortType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.hibernate.graph.GraphSemantic;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomShoesRepositoryImpl implements CustomShoesRepository {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public CustomShoesRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<Shoes> findAllWithBrand() {
        CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Shoes> criteria = cb.createQuery(Shoes.class);

        Root<Shoes> shoes = criteria.from(Shoes.class);
        shoes.fetch("brand", JoinType.INNER);
        shoes.fetch("availabilities", JoinType.LEFT);

        criteria.select(shoes);

        return entityManagerFactory.createEntityManager()
                .createQuery(criteria)
                .getResultList();
    }

    @Override
    public List<Shoes> findAllWithBrand(Integer pageNum, Integer countPerPage) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Long> ids = entityManager
                .createQuery("SELECT s.id FROM Shoes s", Long.class)
                .setFirstResult((pageNum - 1) * countPerPage)
                .setMaxResults(countPerPage)
                .getResultList();

        CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Shoes> criteria = cb.createQuery(Shoes.class);

        Root<Shoes> shoes = criteria.from(Shoes.class);
        shoes.fetch("brand", JoinType.INNER);
        shoes.fetch("availabilities", JoinType.LEFT);

        criteria.select(shoes)
                .where(shoes.get("id").in(ids));

        return entityManager.createQuery(criteria)
                .getResultList();
    }

    @Override
    public Optional<Shoes> findByIdWithBrandAndSize(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Map<String, Object> properties = Map.of(
                GraphSemantic.LOAD.getJakartaHintName(), GraphUtil.shoesWithBrandAndSize(entityManager)
        );
        return Optional.ofNullable(entityManager
                .find(Shoes.class, id, properties));
    }

    @Override
    public Optional<Shoes> findByIdWithAvailability(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Map<String, Object> properties = Map.of(
                GraphSemantic.LOAD.getJakartaHintName(), GraphUtil.shoesWithAvailability(entityManager)
        );
        return Optional.ofNullable(entityManager
                .find(Shoes.class, id, properties));
    }

    @Override
    public List<Shoes> findWithCriteria(List<String> brands, List<Gender> genders, Integer priceMin,
                                        Integer priceMax, Integer sizeMin, Integer sizeMax, SortType sortType,
                                        boolean isContain, Integer pageNum, Integer countPerPage) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = cb.createQuery(Tuple.class);

        Root<Shoes> shoes = criteria.from(Shoes.class);

        List<Predicate> predicates = new ArrayList<>();

        if (brands != null && !brands.isEmpty()) {
            var brand = shoes.join("brand", JoinType.INNER);
            predicates.add(brand.get("name").in(brands));
        }

        if (genders != null && !genders.isEmpty())
            predicates.add(shoes.get("gender").in(genders));

        if (priceMin != null)
            predicates.add(cb.ge(shoes.get("price"), priceMin));

        if (priceMax != null)
            predicates.add(cb.le(shoes.get("price"), priceMax));

        if (isContain || sizeMin != null || sizeMax != null) {
            var availability = shoes.join("availabilities", JoinType.LEFT);
            if (isContain)
                predicates.add(cb.isNotNull(availability.get("id")));

            if (sizeMin != null || sizeMax != null) {
                var size = availability.join("size", JoinType.INNER);
                if (sizeMin != null)
                    predicates.add(cb.ge(size.get("size"), sizeMin));

                if (sizeMax != null)
                    predicates.add(cb.le(size.get("size"), sizeMax));
            }
        }

        criteria.multiselect(shoes.get("id"), shoes.get("price"))
                .distinct(true)
                .where(predicates.toArray(Predicate[]::new));

        setOrderBy(cb, criteria, shoes, sortType);

        List<Long> ids =  entityManager
                .createQuery(criteria)
                .setFirstResult((pageNum - 1) * countPerPage)
                .setMaxResults(countPerPage)
                .getResultList()
                .stream()
                .map(e -> e.get(0, Long.class))
                .toList();

        CriteriaQuery<Shoes> criteriaRes = cb.createQuery(Shoes.class);

        Root<Shoes> shoesRes = criteriaRes.from(Shoes.class);
        shoesRes.fetch("brand", JoinType.INNER);
        shoesRes.fetch("availabilities", JoinType.LEFT);

        criteriaRes.select(shoesRes)
                .where(shoesRes.get("id").in(ids));

        setOrderBy(cb, criteriaRes, shoesRes, sortType);

        return entityManager
                .createQuery(criteriaRes)
                .getResultList();
    }

    private void setOrderBy(CriteriaBuilder cb, CriteriaQuery<?> criteria, Root<?> table, SortType sortType) {
        switch (sortType) {
            case BY_PRICE_ASC -> criteria.orderBy(cb.asc(table.get("price")));
            case BY_PRICE_DES -> criteria.orderBy(cb.desc(table.get("price")));
            case NONE -> criteria.orderBy(cb.desc(table.get("id")));
        }
    }
}
