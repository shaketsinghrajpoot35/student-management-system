package com.smartstudent.main.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDetailsResponseDTO {
    private Long id;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String accountNumber;
    private String accountHolderName;
    private String passbookFilePath;
}
