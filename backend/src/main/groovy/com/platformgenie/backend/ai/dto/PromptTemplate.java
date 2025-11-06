package com.platformgenie.backend.ai.dto;

public enum PromptTemplate {
    AWS("Generate AWS Terraform with resources: %s"),
    AZURE("Generate Azure Terraform with resources: %s"),
    GCP("Generate GCP Terraform with resources: %s");

    private final String template;


    PromptTemplate(String template) {
        this.template = template;
    }

    public String format(String resource) {
        return String.format(template, resource);
    }
}
