package com.sptm.backend.service;

import com.sptm.backend.model.CoreValue;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.CoreValueRepository;
import com.sptm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreValueService {
    @Autowired
    private CoreValueRepository coreValueRepository;
    @Autowired
    private UserRepository userRepository;

    public List<CoreValue> getCoreValuesByUserId(Long userId) {
        return coreValueRepository.findByUserId(userId);
    }

    public CoreValue createCoreValue(Long userId, String text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CoreValue coreValue = new CoreValue();
        coreValue.setUser(user);
        coreValue.setText(cleanText(text));
        return coreValueRepository.save(coreValue);
    }

    public CoreValue updateCoreValue(Long id, String text) {
        CoreValue coreValue = coreValueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Core Value not found"));
        coreValue.setText(cleanText(text));
        return coreValueRepository.save(coreValue);
    }

    private String cleanText(String text) {
        if (text == null)
            return null;
        String cleaned = text.trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned.replace("\\n", "\n");
    }

    public void deleteCoreValue(Long id) {
        coreValueRepository.deleteById(id);
    }
}
