package com.platformgenie.backend.input.service;

import com.platformgenie.backend.input.dto.InfraSpec;
import com.platformgenie.backend.input.dto.ServiceSpec;
import com.platformgenie.backend.input.exception.InvalidSpecException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpecValidatorTest {

    private final SpecValidator validator = new SpecValidator();

    @Test
    void validate_missingProjectName_throwsException() {
        InfraSpec spec = new InfraSpec();
        spec.setServices(Collections.singletonList(new ServiceSpec()));
        assertThatThrownBy(() -> validator.validate(spec))
                .isInstanceOf(InvalidSpecException.class)
                .hasMessageContaining("Project name is required");
    }

    @Test
    void validate_missingServices_throwsException() {
        InfraSpec spec = new InfraSpec();
        spec.setProjectName("test");
        assertThatThrownBy(() -> validator.validate(spec))
                .isInstanceOf(InvalidSpecException.class)
                .hasMessageContaining("At least one service must be defined");
    }

    @Test
    void validate_defaultsAreSet() {
        InfraSpec spec = new InfraSpec();
        spec.setProjectName("test");
        spec.setServices(Collections.singletonList(new ServiceSpec()));
        validator.validate(spec);
        assert spec.getCloudProvider().equals("AWS");
        assert spec.getRegion().equals("us-east-1");
    }
}
