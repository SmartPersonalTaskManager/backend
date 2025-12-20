package com.sptm.backend.service;

import com.sptm.backend.dto.WeeklyStatsDTO;
import com.sptm.backend.model.Task;
import com.sptm.backend.model.User;
import com.sptm.backend.model.WeeklyReview;
import com.sptm.backend.repository.TaskRepository;
import com.sptm.backend.repository.UserRepository;
import com.sptm.backend.repository.WeeklyReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WeeklyReviewRepository weeklyReviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public WeeklyStatsDTO getWeeklyStats(Long userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);

        // For simplicity, we are calculating stats for ALL time for this user in this
        // iteration.
        // In a real scenario, we would filter by date.
        int total = tasks.size();
        int completed = (int) tasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED)
                .count();

        WeeklyStatsDTO stats = new WeeklyStatsDTO();
        stats.setTotalTasks(total);
        stats.setCompletedTasks(completed);
        stats.setCompletionRate(total == 0 ? 0 : (completed * 100) / total);

        return stats;
    }

    @Transactional
    public WeeklyReview createReview(Long userId, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WeeklyReview review = new WeeklyReview();
        review.setUser(user);
        review.setReflectionNotes(notes);
        review.setStartDate(LocalDate.now().minusDays(7)); // Simplified
        review.setEndDate(LocalDate.now());

        return weeklyReviewRepository.save(review);
    }
}
