package com.sptm.backend.repository;

import com.sptm.backend.model.SubMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubMissionRepository extends JpaRepository<SubMission, Long> {
    List<SubMission> findByMissionStatementId(Long missionStatementId);
}
