package com.sptm.backend.service;

import com.sptm.backend.model.Vision;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.VisionRepository;
import com.sptm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisionService {
    @Autowired
    private VisionRepository visionRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Vision> getVisionsByUserId(Long userId) {
        return visionRepository.findByUserId(userId);
    }

    public Vision createVision(Long userId, String text) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Vision vision = new Vision();
        vision.setUser(user);
        vision.setText(text);
        return visionRepository.save(vision);
    }

    public Vision updateVision(Long id, String text) {
        Vision vision = visionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vision not found"));
        vision.setText(text);
        return visionRepository.save(vision);
    }

    public void deleteVision(Long id) {
        visionRepository.deleteById(id);
    }
}
