package com.example.identityService.domain.repository.impl;

import com.devdeli.common.dto.request.EnumSortDirection;
import com.example.identityService.application.DTO.request.UserPageRequest;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.util.StrUtils;
import com.example.identityService.domain.entity.Account;
import com.example.identityService.application.mapper.AccountMapper;
import com.example.identityService.domain.repository.CustomAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AccountRepositoryImpl implements CustomAccountRepository {

    private final AccountMapper accountMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserResponse> search(UserPageRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select a from Account a " + createWhereQuery(request, values) + createOrderQuery(request.getSortedBy(), request.getSortDirection());
        Query query = entityManager.createQuery(sql, Account.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getPage() - 1) * request.getSize());
        query.setMaxResults(request.getSize());
        var res = query.getResultList();
        return accountMapper.toListUserResponse(res);
    }

    @Override
    public Long count(UserPageRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(a) from Account a " + createWhereQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(UserPageRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append(" where a.deleted = false");

        if (request.getQuery() != null && !request.getQuery().trim().isEmpty()) {
            sql.append(" and (")
                    .append(" lower(cast(unaccent(concat(a.id, ' ', a.email, ' ', a.address, ' ', a.fullname)) as String))")
                    .append(" like :keyword ")
                    .append(" )");
            values.put("keyword", StrUtils.encodeKeyword(request.getQuery()));
        }

        if(request.getEmail() != null && !request.getEmail().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.email)")
                    .append(" like :email ")
                    .append(" )");
            values.put("email", request.getEmail().toLowerCase());
        }

        if(request.getFullname() != null && !request.getFullname().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.fullname)")
                    .append(" like :fullname ")
                    .append(" )");
            values.put("fullname", request.getFullname().toLowerCase());
        }

        if(request.getFullname() != null && !request.getFullname().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.fullname)")
                    .append(" like :fullname ")
                    .append(" )");
            values.put("fullname", request.getFullname().toLowerCase());
        }

        if (request.getDob() != null) {
            LocalDate requestDate = request.getDob();
            sql.append(" and a.dob = :dob");
            values.put("dob", requestDate);
        }

        if (request.getYoe() != null) {
            int requestYoe = request.getYoe();
            sql.append(" and a.yoe = :yoe");
            values.put("yoe", requestYoe);
        }

        if (request.getVerified() != null) {
            boolean requestVerified = request.getVerified();
            sql.append(" and a.verified = :verified");
            values.put("verified", requestVerified);
        }

        if (request.getEnable() != null) {
            boolean requestEnable = request.getEnable();
            sql.append(" and a.enable = :enable");
            values.put("enable", requestEnable);
        }

        if(request.getAddress() != null && !request.getAddress().trim().isEmpty()){
            sql.append(" and (")
                    .append(" lower(a.address)")
                    .append(" like :address ")
                    .append(" )");
            values.put("address", request.getAddress().toLowerCase());
        }

        if (request.getGender() != null) {
            String requestGender = request.getGender();
            sql.append(" and a.gender = :gender");
            values.put("gender", requestGender);
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
