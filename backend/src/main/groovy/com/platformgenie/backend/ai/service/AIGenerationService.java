package com.platformgenie.backend.ai.service;

import com.platformgenie.backend.ai.dto.InfrastructureSpec;
import com.platformgenie.backend.ai.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIGenerationService {

    private final OpenAiChatModel openAiChatModel;
    private final PromptService promptService;

    /**
     * Generate Terraform snippets for all required files using AI
     */
    public Map<String, String> generateTerraformFiles(InfrastructureSpec spec) {
        Map<String, String> snippets = new HashMap<>();

        // Build prompt from user spec
        String prompt = promptService.buildPrompt(spec);

        // Generate main Terraform code
        String mainTf = openAiChatModel.call("Generate main.tf Terraform code:\n" + prompt);
        snippets.put("main.tf", mainTf);

        // Generate provider.tf
        String providerTf = openAiChatModel.call("Generate provider.tf Terraform code:\n" + prompt);
        snippets.put("provider.tf", providerTf);

        // Generate outputs.tf
        String outputsTf = openAiChatModel.call("Generate outputs.tf Terraform code:\n" + prompt);
        snippets.put("outputs.tf", outputsTf);

        // Generate variables.tf if needed
        String variablesTf = openAiChatModel.call("Generate variables.tf Terraform code:\n" + prompt);
        snippets.put("variables.tf", variablesTf);

        return snippets;
    }
}
