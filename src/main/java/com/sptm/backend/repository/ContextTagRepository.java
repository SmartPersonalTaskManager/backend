package com.sptm.backend.repository;

import com.sptm.backend.model.ContextTag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContextTagRepository extends JpaRepository<ContextTag, Long> {
    List<ContextTag> findByUserId(Long userId);
}
