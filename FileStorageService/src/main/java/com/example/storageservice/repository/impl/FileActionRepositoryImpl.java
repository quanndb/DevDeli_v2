package com.example.storageservice.repository.impl;

import com.devdeli.common.dto.request.EnumSortDirection;
import com.devdeli.common.dto.request.FileActionPageRequest;
import com.example.storageservice.entity.FileAction;
import com.example.storageservice.repository.CustomFileActionRepository;
import com.example.storageservice.util.StrUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FileActionRepositoryImpl implements CustomFileActionRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<FileAction> search(FileActionPageRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select a from FileAction a " + createWhereQuery(request, values) + createOrderQuery(request.getSortedBy(), request.getSortDirection());
        Query query = entityManager.createQuery(sql, FileAction.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getPage() - 1) * request.getSize());
        query.setMaxResults(request.getSize());
        return query.getResultList();
    }

    @Override
    public Long count(FileActionPageRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(a) from FileAction a " + createWhereQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(FileActionPageRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append(" where a.id is not null");

        if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
            sql.append(" and (")
                    .append(" lower(cast(unaccent(concat(a.id, ' ', a.fileId, ' ', a.action)) as String))")
                    .append(" like :keyword ")
                    .append(" )");
            values.put("keyword", StrUtils.encodeKeyword(request.getQuery()));
        }

        if(request.getFileId() != null && !request.getFileId().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.fileId)")
                    .append(" like :fileId ")
                    .append(" )");
            values.put("fileId", request.getFileId().toLowerCase());
        }

        if(request.getAction() != null && !request.getAction().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.action)")
                    .append(" like :action ")
                    .append(" )");
            values.put("action", request.getAction().toLowerCase());
        }

        if (request.getCreatedDate() != null) {
            LocalDate requestDate = request.getCreatedDate();
            LocalDateTime startOfDay = requestDate.atStartOfDay();
            LocalDateTime endOfDay = requestDate.atTime(LocalTime.MAX);

            sql.append(" and (a.createdDate between :startDate and :endDate)");
            values.put("startDate", startOfDay);
            values.put("endDate", endOfDay);
        }

        if(request.getCreatedBy() != null && !request.getCreatedBy().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.createdBy)")
                    .append(" like :createdBy ")
                    .append(" )");
            values.put("createdBy", request.getCreatedBy().toLowerCase());
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
