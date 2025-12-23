package com.sptm.backend.repository;

import com.sptm.backend.model.CoreValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoreValueRepository extends JpaRepository<CoreValue, Long> {
    List<CoreValue> findByUserId(Long userId);
}
