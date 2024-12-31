package com.example.identityService.domain.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginQuery {
    String email;
    String password;
}
