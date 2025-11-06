package com.platformgenie.backend.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class InfrastructureSpec {
    private String id; // optional for logging/versioning
    private String cloudProvider;
    private String environment;
    private String region;
    private boolean databaseRequired;
    private String databaseType;
    private List<Resource> resource;

    @Data
    public static class Resource {
        private String type;
        private String name;
    }
}
