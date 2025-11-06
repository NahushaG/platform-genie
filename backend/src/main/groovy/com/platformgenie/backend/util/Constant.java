package com.platformgenie.backend.util;

public  class Constant {
    public static final String INPUT_DEFAULT_INSTANCE_TYPE="t3.micro";
    public static final String AI_PROMPT_HEADER = "You are an expert Terraform generator.\n";
    public static final String AI_CLOUD_LINE = "Target Cloud Provider: %s\n";
    public static final String AI_RESOURCES_HEADER = "Resources:\n";
    public static final String AI_RESOURCE_LINE = "- %s: %s\n";
    public static final String AI_DATABASE_LINE = "Include database module: %s\n";
    public static final String AI_ENV_LINE = "Environment: %s\n";
    public static final String ASSEMBLY_MAIN_TF = "main.tf";
    public static final String ASSEMBLY_VARIABLES_TF = "variables.tf";
    public static final String ASSEMBLY_OUTPUTS_TF = "outputs.tf";
    public static final String ASSEMBLY_PROVIDER_TF = "provider.tf";
    public static final String ASSEMBLY_TFVARS = "terraform.tfvars";
}
