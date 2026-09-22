package com.civicvote.election.repository;

import com.civicvote.election.entity.Election;
import com.civicvote.election.entity.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findByStatus(ElectionStatus status);
    List<Election> findByStatusIn(List<ElectionStatus> statuses);
}
