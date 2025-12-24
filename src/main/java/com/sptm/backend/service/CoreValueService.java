package com.sptm.backend.service;

import com.sptm.backend.model.CoreValue;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.CoreValueRepository;
import com.sptm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CoreValueService {
    @Autowired
    private CoreValueRepository coreValueRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CoreValue> getCoreValuesByUserId(Long userId) {
        return coreValueRepository.findByUserId(userId);
    }

    public CoreValue createCoreValue(Long userId, String text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CoreValue coreValue = new CoreValue();
        coreValue.setUser(user);
        coreValue.setText(text);
        return coreValueRepository.save(coreValue);
    }

    public CoreValue updateCoreValue(Long id, String text) {
        CoreValue coreValue = coreValueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Core Value not found"));
        coreValue.setText(text);
        return coreValueRepository.save(coreValue);
    }

    public void deleteCoreValue(Long id) {
        coreValueRepository.deleteById(id);
    }
}
