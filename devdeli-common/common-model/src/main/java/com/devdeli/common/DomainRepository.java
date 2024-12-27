package com.devdeli.common;

import java.util.List;
import java.util.Optional;

public interface DomainRepository<D, ID> {
    Optional<D> findById(ID id);
    D getById(ID id);
    List<D> findAllByIds(List<ID> ids);
    boolean save(D domain);
    boolean saveAll(List<D> domains);
    boolean delete(D domain);
    boolean deleteById(ID id);
    boolean existsById(ID id);
}
