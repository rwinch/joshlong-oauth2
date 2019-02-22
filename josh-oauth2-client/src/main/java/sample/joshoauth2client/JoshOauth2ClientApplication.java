package sample.joshoauth2client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@SpringBootApplication
@RestController
public class JoshOauth2ClientApplication {
	@Autowired
	WebClient webClient;

	@Bean
	WebClient webClient(ReactiveClientRegistrationRepository clients,
			ServerOAuth2AuthorizedClientRepository authz) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
			new ServerOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
		// to do implicit set that
//		oauth2.setDefaultOAuth2AuthorizedClient(true);
//		oauth2.setDefaultClientRegistrationId("keycloak");
		return WebClient
				.builder()
				.filter(oauth2)
				.build();
	}

	@GetMapping("/explicit")
	Mono<String> user(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client) {
		return webClient.get()
			.uri("http://localhost:9090/me")
			.attributes(oauth2AuthorizedClient(client))
			.retrieve()
			.bodyToMono(String.class);

	}



	@GetMapping("/implicit")
	Mono<String> implicit() {
		return webClient.get()
				.uri("http://localhost:9090/me")
				.retrieve()
				.bodyToMono(String.class);

	}

	// this is including oauth2 great for a service since it doesn't need to get registeredclient
	@GetMapping("/good-service")
	Mono<String> goodService() {
		// any interaction with OAuth2 token will refresh if expired or about to expire (implicit or explit both do this)
		return webClient.get()
				.uri("http://localhost:9090/me")
				.attributes(clientRegistrationId("keycloak"))
				.retrieve()
				.bodyToMono(String.class);

	}

	public static void main(String[] args) {
		SpringApplication.run(JoshOauth2ClientApplication.class, args);
	}

}
