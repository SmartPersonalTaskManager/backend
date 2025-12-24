package com.sptm.backend.controller;

import com.sptm.backend.model.CoreValue;
import com.sptm.backend.service.CoreValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core-values")
public class CoreValueController {
    @Autowired
    private CoreValueService coreValueService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CoreValue>> getCoreValuesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(coreValueService.getCoreValuesByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<CoreValue> createCoreValue(@RequestParam Long userId,
            @RequestBody java.util.Map<String, String> payload) {
        return ResponseEntity.ok(coreValueService.createCoreValue(userId, payload.get("text")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoreValue> updateCoreValue(@PathVariable Long id,
            @RequestBody java.util.Map<String, String> payload) {
        return ResponseEntity.ok(coreValueService.updateCoreValue(id, payload.get("text")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoreValue(@PathVariable Long id) {
        coreValueService.deleteCoreValue(id);
        return ResponseEntity.noContent().build();
    }
}
