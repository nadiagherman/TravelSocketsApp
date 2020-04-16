package repository;


import entities.Entity;

import java.util.List;

public interface CrudRepository<ID, E extends Entity<ID>> {
    E findOne(ID id);

    Iterable<E> findAll();

    E save(E e);

    E delete(ID id);

    int size();
}