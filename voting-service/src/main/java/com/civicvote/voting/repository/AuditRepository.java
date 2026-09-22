package com.civicvote.voting.repository;

import com.civicvote.voting.entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditRepository extends JpaRepository<AuditRecord, Long> {
    List<AuditRecord> findByEntityType(String entityType);
    List<AuditRecord> findByEventType(String eventType);
}
