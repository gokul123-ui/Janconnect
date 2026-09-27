package com.janconnect.controller;

import com.janconnect.dto.AiClassifyRequest;
import com.janconnect.dto.AiClassifyResponse;
import com.janconnect.service.AiClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiClassifierService aiClassifierService;

    @PostMapping("/classify")
    public ResponseEntity<AiClassifyResponse> classify(@RequestBody AiClassifyRequest request) {
        AiClassifyResponse response = aiClassifierService.classify(request);
        return ResponseEntity.ok(response);
    }
}
