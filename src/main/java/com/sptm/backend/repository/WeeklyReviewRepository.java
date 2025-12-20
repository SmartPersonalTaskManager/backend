package com.sptm.backend.repository;

import com.sptm.backend.model.WeeklyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WeeklyReviewRepository extends JpaRepository<WeeklyReview, Long> {
    List<WeeklyReview> findByUserIdOrderByStartDateDesc(Long userId);
}
