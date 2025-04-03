package com.auditexample.api.audit.configs;

import com.auditexample.api.common.security.client.token.TokenConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnExpression("${core.audit.enable:false} && !'${core.audit.auth.clientId:}'.isEmpty()")
public class CoreApiClientConfig {

  @Bean
  @ConditionalOnProperty(name = "core.audit.logs.url")
  public WebClient coreAuditLogApiClient(@Value("${core.audit.logs.url}") String url,
      ServerOAuth2AuthorizedClientExchangeFilterFunction oauth) {
    return WebClient.builder()
        .baseUrl(url)
        .filter(oauth)
        .build();
  }

  @Bean
  public ServerOAuth2AuthorizedClientExchangeFilterFunction oauth(
      ReactiveClientRegistrationRepository reactiveClientRegistrationRepository) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
        reactiveClientRegistrationRepository, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
    oauth.setDefaultOAuth2AuthorizedClient(true);
    oauth.setDefaultClientRegistrationId(TokenConstants.UAA_CLIENT_REGISTRATION_ID);
    return oauth;
  }

  @Bean
  public ReactiveClientRegistrationRepository reactiveClientRegistrationRepository(
      ClientRegistration clientRegistration) {
    return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository(
      ClientRegistration clientRegistration) {
    return new InMemoryClientRegistrationRepository(clientRegistration);
  }

  @Bean
  public ClientRegistration clientRegistration(
      @Value("${core.audit.auth.tokenUrl}") String tokenUri,
      @Value("${core.audit.auth.userInfoUri}") String userInfoUri,
      @Value("${core.audit.auth.clientId}") String clientId,
      @Value("${core.audit.auth.clientSecret}") String clientSecret) {
    return ClientRegistration
        .withRegistrationId("uaa")
        .tokenUri(tokenUri)
        .userInfoUri(userInfoUri)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .build();
  }

  @Bean
  public OAuth2AuthorizedClientService authorizedClientService(
      ClientRegistrationRepository clientRegistrationRepository) {
    return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
  }

}
