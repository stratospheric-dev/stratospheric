package dev.stratospheric.registration;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
public class CognitoRegistrationService implements RegistrationService {

  private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
  private final String userPooldId;

  public CognitoRegistrationService(
    CognitoIdentityProviderClient cognitoIdentityProviderClient,
    @Value("${COGNITO_USER_POOL_ID}") String userPoolId) {
    this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
    this.userPooldId = userPoolId;
  }

  @Override
  public void registerUser(Registration registration) {
    AdminCreateUserRequest registrationRequest = new AdminCreateUserRequest()
      .withUserPoolId(userPooldId)
      .withUsername(registration.getUsername())
      .withUserAttributes(
        new AttributeType().withName("email").withValue(registration.getEmail()),
        new AttributeType().withName("name").withValue(registration.getUsername()),
        new AttributeType().withName("email_verified").withValue("true")
      )
      .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
      .withForceAliasCreation(Boolean.FALSE);

    cognitoIdentityProviderClient.adminCreateUser(registrationRequest);
  }
}
