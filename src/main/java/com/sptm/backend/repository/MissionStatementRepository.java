package com.sptm.backend.repository;

import com.sptm.backend.model.MissionStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionStatementRepository extends JpaRepository<MissionStatement, Long> {
    List<MissionStatement> findByUserId(Long userId);

    Optional<MissionStatement> findByUserIdAndVersion(Long userId, Integer version);
}
