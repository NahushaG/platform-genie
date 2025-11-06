package com.platformgenie.backend.input.service;

import com.platformgenie.backend.input.dto.InfraSpec;
import com.platformgenie.backend.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InputProcessingService {
    private final SpecValidator validator;

    public InfraSpec processInput(InfraSpec spec) {
        validator.validate(spec);

        spec.getServices()
                .forEach(serviceSpec -> {
                    if (serviceSpec.getInstanceType() == null) {
                        serviceSpec.setType(Constant.INPUT_DEFAULT_INSTANCE_TYPE);
                    }
                });
        return spec;
    }
}
