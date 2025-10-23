package com.apiauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String docNumber;
    private String username;
    private String fullName;
    private Boolean loggedin;
    private String createdAt;
    private String updatedAt;
}
