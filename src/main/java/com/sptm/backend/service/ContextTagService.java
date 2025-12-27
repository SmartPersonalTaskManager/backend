package com.sptm.backend.service;

import com.sptm.backend.model.ContextTag;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.ContextTagRepository;
import com.sptm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContextTagService {
    private static final Logger logger = LoggerFactory.getLogger(ContextTagService.class);

    @Autowired
    private ContextTagRepository contextTagRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ContextTag> getContextTagsByUserId(Long userId) {
        logger.info("Fetching contexts for userId: {}", userId);
        List<ContextTag> tags = contextTagRepository.findByUserId(userId);
        logger.info("Found {} contexts for userId: {}", tags.size(), userId);
        return tags;
    }

    @Transactional
    public ContextTag createContextTag(Long userId, String name, String icon) {
        logger.info("Creating context for userId: {}, name: {}", userId, name);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        ContextTag contextTag = new ContextTag();
        contextTag.setUser(user);
        contextTag.setName(name);
        contextTag.setIcon(icon);
        ContextTag saved = contextTagRepository.save(contextTag);
        logger.info("Saved context id: {}", saved.getId());
        return saved;
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
