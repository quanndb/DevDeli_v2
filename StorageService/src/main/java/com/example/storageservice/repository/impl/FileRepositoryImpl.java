package com.example.storageservice.repository.impl;

import com.example.storageservice.DTO.EnumSortDirection;
import com.example.storageservice.DTO.request.FilePageRequest;
import com.example.storageservice.DTO.response.FileResponse;
import com.example.storageservice.entity.File;
import com.example.storageservice.mapper.FileMapper;
import com.example.storageservice.repository.CustomFileRepository;
import com.example.storageservice.util.StrUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FileRepositoryImpl implements CustomFileRepository {
    private final FileMapper fileMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FileResponse> search(FilePageRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select a from File a " + createWhereQuery(request, values) + createOrderQuery(request.getSortedBy(), request.getSortDirection());
        Query query = entityManager.createQuery(sql, File.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getPage() - 1) * request.getSize());
        query.setMaxResults(request.getSize());
        var res = query.getResultList();
        return fileMapper.toListFileResponse(res);
    }

    @Override
    public Long count(FilePageRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(a) from File a " + createWhereQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(FilePageRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append(" where a.deleted = false");

        if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
            sql.append(" and (")
                    .append(" lower(cast(unaccent(concat(a.id, ' ', a.ownerId, ' ', a.name, ' ', a.path, ' ', a.type)) as String))")
                    .append(" like :keyword ")
                    .append(" )");
            values.put("keyword", StrUtils.encodeKeyword(request.getQuery()));
        }

        return sql.toString();
    }

    public StringBuilder createOrderQuery(String sortBy, EnumSortDirection sortDirection) {
        StringBuilder hql = new StringBuilder();

        String direction = (sortDirection == EnumSortDirection.ASC) ? "asc" : "desc";

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            hql.append(" order by a.").append(sortBy.trim()).append(" ").append(direction);
        } else {
            hql.append(" order by a.updatedDate desc");
        }
        return hql;
    }
}
