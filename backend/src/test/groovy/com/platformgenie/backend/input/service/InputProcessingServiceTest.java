package com.platformgenie.backend.input.service;

import com.platformgenie.backend.input.dto.InfraSpec;
import com.platformgenie.backend.input.dto.ServiceSpec;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class InputProcessingServiceTest {

    private final SpecValidator validator = new SpecValidator();
    private final InputProcessingService service = new InputProcessingService(validator);

    @Test
    void processInput_setsDefaultInstanceType() {
        InfraSpec spec = new InfraSpec();
        spec.setProjectName("test");
        spec.setServices(Collections.singletonList(new ServiceSpec("web", "web", null, 1)));

        InfraSpec processed = service.processInput(spec);

        assertThat(processed.getServices().get(0).getType()).isEqualTo("t3.micro");
    }
}
