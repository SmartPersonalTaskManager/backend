package com.sptm.backend.service;

import com.sptm.backend.model.ContextTag;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.ContextTagRepository;
import com.sptm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextTagService {
    @Autowired
    private ContextTagRepository contextTagRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ContextTag> getContextTagsByUserId(Long userId) {
        return contextTagRepository.findByUserId(userId);
    }

    public ContextTag createContextTag(Long userId, String name, String icon) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ContextTag contextTag = new ContextTag();
        contextTag.setUser(user);
        contextTag.setName(name);
        contextTag.setIcon(icon);
        return contextTagRepository.save(contextTag);
    }

    public void deleteContextTag(Long id) {
        contextTagRepository.deleteById(id);
    }

    // Check if user has context tags, if not create defaults (optional, logic can
    // be in frontend or here)
    // For now, frontend handles defaults logic, but we might want to initialize
    // upon user creation.
    // Let's stick to simple CRUD for now.
}
