package com.civicvote.voting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_records", indexes = {
    @Index(name = "idx_audit_entity", columnList = "entity_id"),
    @Index(name = "idx_audit_event", columnList = "event_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "integrity_hash", nullable = false, length = 64)
    private String integrityHash;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}
