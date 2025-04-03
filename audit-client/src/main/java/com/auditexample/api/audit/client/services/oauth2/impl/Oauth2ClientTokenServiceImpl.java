package com.auditexample.api.audit.client.services.oauth2.impl;

import com.auditexample.api.audit.client.model.UaaResponse;
import com.auditexample.api.audit.client.services.oauth2.Oauth2ClientTokenService;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-06-08<br/>
 */

@Service
@Slf4j
public class Oauth2ClientTokenServiceImpl implements Oauth2ClientTokenService {

  private static final AnonymousAuthenticationToken ANONYMOUS_USER_TOKEN =
      new AnonymousAuthenticationToken(
          "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_USER"));

  @Autowired
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @Autowired
  private ClientRegistration clientRegistration;

  @Autowired
  @Qualifier("uaaWebClient")
  private WebClient webClient;

  @Override
  public String getTokenValue(@NonNull String clientRegistrationId) {
    OAuth2AccessToken accessToken = getToken(clientRegistrationId);
    return accessToken.getTokenValue();
  }

  @Override
  public OAuth2AccessToken getToken(@NonNull String clientRegistrationId) {
    log.debug("Loading token for client registration id: {}", clientRegistrationId);
    var authorizedClient =
        oAuth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId,
            (String) ANONYMOUS_USER_TOKEN.getPrincipal());
    if (Objects.isNull(authorizedClient) || authorizedClient.getAccessToken().getExpiresAt().isBefore(Instant.now())) {
      Instant now = Instant.now();
      var uaaToken = Objects.requireNonNull(requestUaaToken(clientRegistration.getClientId(), clientRegistration.getClientSecret()));
      OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(TokenType.BEARER, uaaToken.getToken(), now, now.plus(uaaToken.getExpiresIn(), ChronoUnit.SECONDS), Set
          .of(uaaToken.getScope().split(" ")));
      OAuth2AuthorizedClient oAuth2AuthorizedClient = new OAuth2AuthorizedClient(clientRegistration,
          (String) ANONYMOUS_USER_TOKEN.getPrincipal(), oAuth2AccessToken);
      oAuth2AuthorizedClientService.saveAuthorizedClient(oAuth2AuthorizedClient, ANONYMOUS_USER_TOKEN);
      return oAuth2AccessToken;
    } else {
      return authorizedClient.getAccessToken();
    }
  }

  private UaaResponse requestUaaToken(String clientId, String clientSecret) {
    return webClient.method(HttpMethod.POST)
        .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
        .exchange()
        .blockOptional(Duration.of(1500, ChronoUnit.MILLIS))
        .map(
            cr -> {
              if (!cr.statusCode().is2xxSuccessful()) {
                log.error(
                    "Problem talking to UAA for token: "
                        + cr.statusCode().value()
                        + " "
                        + cr.bodyToMono(String.class).block());
                return null;
              }
              return cr.bodyToMono(UaaResponse.class).block();
            })
        .orElse(null);
  }
}
