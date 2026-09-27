package com.janconnect.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiClassifyRequest {
    private String text;
    private String language;
}

