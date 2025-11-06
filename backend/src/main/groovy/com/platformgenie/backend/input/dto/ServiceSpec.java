package com.platformgenie.backend.input.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceSpec {
    private String name;
    private String type;
    private String instanceType;
    private int replicas;
}
