package com.sptm.backend.repository;

import com.sptm.backend.model.Vision;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisionRepository extends JpaRepository<Vision, Long> {
    List<Vision> findByUserId(Long userId);
}
