package com.sptm.backend.controller;

import com.sptm.backend.model.ContextTag;
import com.sptm.backend.service.ContextTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contexts")
public class ContextTagController {
    @Autowired
    private ContextTagService contextTagService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ContextTag>> getContextTagsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(contextTagService.getContextTagsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ContextTag> createContextTag(@RequestParam Long userId,
            @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String icon = payload.get("icon");
        return ResponseEntity.ok(contextTagService.createContextTag(userId, name, icon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContextTag(@PathVariable Long id) {
        contextTagService.deleteContextTag(id);
        return ResponseEntity.noContent().build();
    }
}
