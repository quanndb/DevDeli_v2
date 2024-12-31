package com.example.identityService.domain.query;

import com.devdeli.common.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GetUserPageQuery extends PageRequest {
    private String id = null;
    private String email = null;
    private String fullname = null;
    private LocalDate dob = null;
    private Integer yoe = null;
    private Boolean verified = null;
    private Boolean enable = null;
    private Boolean deleted = null;
    private String gender = null;
    private String address = null;
    private String cloudImageId = null;
    private String cloudImageUrl = null;
}
