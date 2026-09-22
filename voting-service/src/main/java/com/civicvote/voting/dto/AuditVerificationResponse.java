package com.civicvote.voting.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditVerificationResponse {
    private int totalRecordsChecked;
    private int validRecords;
    private int invalidRecords;
    private String integrityStatus;
}
