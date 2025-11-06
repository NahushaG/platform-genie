package com.platformgenie.backend

import com.platformgenie.backend.ai.config.OpenAiConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(OpenAiConfig.class)
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
