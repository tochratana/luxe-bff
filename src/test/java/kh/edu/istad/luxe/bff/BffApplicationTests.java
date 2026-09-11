package kh.edu.istad.luxe.bff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BffApplicationTests.TestApplication.class)
class BffApplicationTests {

	@SpringBootConfiguration
	static class TestApplication {
	}

	@Test
	void contextLoads() {
	}

}
