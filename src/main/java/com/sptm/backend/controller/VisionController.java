package com.sptm.backend.controller;

import com.sptm.backend.model.Vision;
import com.sptm.backend.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visions")
public class VisionController {
    @Autowired
    private VisionService visionService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Vision>> getVisionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(visionService.getVisionsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Vision> createVision(@RequestParam Long userId,
            @RequestBody java.util.Map<String, String> payload) {
        return ResponseEntity.ok(visionService.createVision(userId, payload.get("text")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vision> updateVision(@PathVariable Long id,
            @RequestBody java.util.Map<String, String> payload) {
        return ResponseEntity.ok(visionService.updateVision(id, payload.get("text")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVision(@PathVariable Long id) {
        visionService.deleteVision(id);
        return ResponseEntity.noContent().build();
    }
}
