package com.auditexample.api.audit.client.services.oauth2;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

/**
 * Created at Auditexample<br/> User: Victor Chernodon<br/> Date: 2020-06-08<br/>
 *
 * OAuth2 Client Token service is responsible for resolving the oauth token for client credentials authorization flow.
 * It retrieves the token relying on underlying Spring Security, which in turn takes care of calling the authorization
 * server endpoint for obtaining the token and renewing it when needed.
 */

public interface Oauth2ClientTokenService {

  String getTokenValue(String clientRegistrationId);

  OAuth2AccessToken getToken(String clientRegistrationId);

}
