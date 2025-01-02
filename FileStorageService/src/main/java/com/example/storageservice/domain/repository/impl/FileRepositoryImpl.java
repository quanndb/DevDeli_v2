package com.example.storageservice.domain.repository.impl;

import com.devdeli.common.dto.request.EnumSortDirection;
import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.FileResponse;
import com.example.storageservice.domain.File;
import com.example.storageservice.domain.FileAction;
import com.example.storageservice.application.mapper.FileMapper;
import com.example.storageservice.domain.repository.CustomFileRepository;
import com.example.storageservice.domain.repository.FileActionRepository;
import com.example.storageservice.application.util.StrUtils;
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
import java.util.UUID;

@RequiredArgsConstructor
public class FileRepositoryImpl implements CustomFileRepository {
    private final FileMapper fileMapper;
    private final FileActionRepository fileActionRepository;

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

    @Override
    public void saveFileImportAction(List<File> files) {
        List<FileAction> saved = files.stream()
                .map(item -> FileAction.builder()
                        .fileId(UUID.fromString(item.getPath()))
                        .action("Save file")
                        .build())
                .toList();
        fileActionRepository.saveAll(saved);
    }

    @Override
    public void saveFileExportAction(String fileName) {
        fileActionRepository.save(FileAction.builder()
                        .fileId(UUID.fromString(fileName))
                        .action("Get file")
                .build());
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

        if(request.getName() != null && !request.getName().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.name)")
                    .append(" like :name ")
                    .append(" )");
            values.put("name", request.getName().toLowerCase());
        }

        if(request.getType() != null && !request.getType().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.type)")
                    .append(" like :type ")
                    .append(" )");
            values.put("type", request.getType().toLowerCase());
        }

        if(request.getOwnerId() != null && !request.getOwnerId().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.ownerId)")
                    .append(" like :ownerId ")
                    .append(" )");
            values.put("ownerId", request.getOwnerId().toLowerCase());
        }

        if (request.getCreatedDate() != null) {
            LocalDate requestDate = request.getCreatedDate();
            LocalDateTime startOfDay = requestDate.atStartOfDay();
            LocalDateTime endOfDay = requestDate.atTime(LocalTime.MAX);

            sql.append(" and (a.createdDate between :startDate and :endDate)");
            values.put("startDate", startOfDay);
            values.put("endDate", endOfDay);
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
