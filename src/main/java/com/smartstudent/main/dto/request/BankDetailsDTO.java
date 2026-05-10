package com.smartstudent.main.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BankDetailsDTO {

    @Size(max = 100)
    private String bankName;

    @Size(max = 100)
    private String branchName;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;

    @Size(max = 30)
    private String accountNumber;

    @Size(max = 100)
    private String accountHolderName;
}
