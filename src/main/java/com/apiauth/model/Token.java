package com.apiauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private Long id;
    private Long userId;
    private String token;
    private String createdAt;
}
