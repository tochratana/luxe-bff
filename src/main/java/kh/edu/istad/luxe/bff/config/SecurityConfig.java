package kh.edu.istad.luxe.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class SecurityConfig {

    @Value("${luxe.ui.url:http://localhost:3000}")
    private String uiUrl;

    @Value("${luxe.admin.public-url:http://localhost:3001}")
    private String adminUrl;

    @Bean
    SecurityWebFilterChain apiGateway(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
        http.authorizeExchange(exchange -> exchange
                .pathMatchers("/admin/**").authenticated()
                .anyExchange().permitAll());
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        http.oauth2Login(Customizer.withDefaults());

        http.logout(logout -> logout
                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(
                        HttpMethod.POST,
                        "/logout",
                        "/admin/logout"
                ))
                .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)));

        return http.build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedServerLogoutSuccessHandler uiLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        uiLogoutSuccessHandler.setPostLogoutRedirectUri(uiUrl);

        OidcClientInitiatedServerLogoutSuccessHandler adminLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        adminLogoutSuccessHandler.setPostLogoutRedirectUri(adminUrl);

        return (exchange, authentication) -> {
            String logoutPath = exchange.getExchange().getRequest().getPath().value();
            ServerLogoutSuccessHandler selectedHandler = "/admin/logout".equals(logoutPath)
                    ? adminLogoutSuccessHandler
                    : uiLogoutSuccessHandler;
            return selectedHandler.onLogoutSuccess(exchange, authentication);
        };
    }
}
