package com.sptm.backend.repository;

import com.sptm.backend.model.ContextTag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContextTagRepository extends JpaRepository<ContextTag, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT c FROM ContextTag c WHERE c.user.id = :userId")
    List<ContextTag> findByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);
}
