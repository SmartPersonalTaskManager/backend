package com.sptm.backend.service;

import com.sptm.backend.dto.TaskDTO;
import com.sptm.backend.model.SubMission;
import com.sptm.backend.model.Task;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.SubMissionRepository;
import com.sptm.backend.repository.TaskRepository;
import com.sptm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubMissionRepository subMissionRepository;

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        User user = userRepository.findById(taskDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setUser(user);
        task.setStatus(Task.TaskStatus.NOT_STARTED);
        task.setContext(taskDTO.getContext());
        task.setInbox(taskDTO.isInbox());
        task.setArchived(taskDTO.isArchived());
        task.setCreatedAt(java.time.LocalDateTime.now());

        // Assign SubMission if provided
        if (taskDTO.getSubMissionId() != null) {
            SubMission subMission = subMissionRepository.findById(taskDTO.getSubMissionId())
                    .orElseThrow(() -> new RuntimeException("SubMission not found"));
            task.setSubMission(subMission);
            task.setMission(subMission.getMissionStatement());
        }

        // Covey Logic
        if (taskDTO.getPriority() == null) {
            task.setPriority(determinePriority(taskDTO.isUrgent(), taskDTO.isImportant()));
        } else {
            task.setPriority(taskDTO.getPriority());
        }

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByUser(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Task.TaskPriority determinePriority(boolean urgent, boolean important) {
        if (urgent && important)
            return Task.TaskPriority.URGENT_IMPORTANT;
        if (!urgent && important)
            return Task.TaskPriority.NOT_URGENT_IMPORTANT;
        if (urgent && !important)
            return Task.TaskPriority.URGENT_NOT_IMPORTANT;
        return Task.TaskPriority.NOT_URGENT_NOT_IMPORTANT;
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setUserId(task.getUser().getId());
        dto.setContext(task.getContext());
        dto.setInbox(task.isInbox());
        dto.setArchived(task.isArchived());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setCreatedAt(task.getCreatedAt());

        if (task.getSubMission() != null) {
            dto.setSubMissionId(task.getSubMission().getId());
        }
        if (task.getMission() != null) {
            dto.setMissionId(task.getMission().getId());
        }
        return dto;
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (taskDTO.getTitle() != null)
            task.setTitle(taskDTO.getTitle());
        if (taskDTO.getDescription() != null)
            task.setDescription(taskDTO.getDescription());
        if (taskDTO.getDueDate() != null)
            task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getStatus() != null)
            task.setStatus(taskDTO.getStatus());

        // Update new fields if not null/default?
        // For boolean, strict update is tricky if we don't know if it's "set" or
        // "default".
        // But for PUT, we usually expect full replace or we trust the DTO values.
        // Let's assume frontend sends correct state.
        System.out.println("Updating task " + taskId + " - isArchived: " + taskDTO.isArchived() + ", completedAt: "
                + taskDTO.getCompletedAt());
        task.setContext(taskDTO.getContext());
        task.setInbox(taskDTO.isInbox());
        task.setArchived(taskDTO.isArchived());
        task.setCompletedAt(taskDTO.getCompletedAt());

        // Update Priority if changed explicitly or if urgent/important flags provided
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        } else if (taskDTO.isUrgent() || taskDTO.isImportant()) {
            task.setPriority(determinePriority(taskDTO.isUrgent(), taskDTO.isImportant()));
        }

        // Update SubMission and Mission if provided
        if (taskDTO.getSubMissionId() != null) {
            SubMission subMission = subMissionRepository.findById(taskDTO.getSubMissionId())
                    .orElseThrow(() -> new RuntimeException("SubMission not found"));
            task.setSubMission(subMission);
            task.setMission(subMission.getMissionStatement());
        }

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found");
        }
        taskRepository.deleteById(taskId);
    }
}
