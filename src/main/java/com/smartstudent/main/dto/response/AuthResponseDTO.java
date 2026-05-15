package com.smartstudent.main.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private Long id;
    private String token;
    private String tokenType;
    private String username;
    private String fullName;
    private String schoolName;
    private String role;
    private String schoolCode;
    @JsonProperty("isApproved")
    private boolean isApproved;
    private Long expiresIn;
}
