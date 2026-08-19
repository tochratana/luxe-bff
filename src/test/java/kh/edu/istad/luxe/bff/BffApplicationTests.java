package kh.edu.istad.luxe.bff;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "KEYCLOAK_CLIENT_SECRET=test-secret")
class BffApplicationTests {

	@Autowired
	private ReactiveClientRegistrationRepository clientRegistrationRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void adminLoginUsesTheAdminPkceClientAndCallback() {
		ClientRegistration registration = clientRegistrationRepository
				.findByRegistrationId("keycloak-admin")
				.block();

		assertNotNull(registration);
		assertEquals("luxe-admin", registration.getClientId());
		assertEquals(ClientAuthenticationMethod.NONE, registration.getClientAuthenticationMethod());
		assertEquals(
				"https://admin.luxe-kh.online/bff/login/oauth2/code/keycloak-admin",
				registration.getRedirectUri()
		);
	}

}
