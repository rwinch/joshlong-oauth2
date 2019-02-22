package com.example.demo;

import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @author Rob Winch
 */
@RestController
public class UserController {
	@GetMapping("/me")
	Mono<String> principal(Mono<JwtAuthenticationToken> t) {
//		return t.map(token -> token.getToken().getClaimAsString(StandardClaimNames.EMAIL));
		return t.map(token -> token.getName());
	}
}
