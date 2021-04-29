package dev.stratospheric.todoapp.registration;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import io.micrometer.core.annotation.Counted;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("aws")
public class CognitoRegistrationService implements RegistrationService {

  private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;
  private final String userPooldId;

  public CognitoRegistrationService(
    AWSCognitoIdentityProvider awsCognitoIdentityProvider,
    @Value("${COGNITO_USER_POOL_ID}") String userPoolId) {
    this.awsCognitoIdentityProvider = awsCognitoIdentityProvider;
    this.userPooldId = userPoolId;
  }

  @Counted(
    value = "stratospheric.registration.users",
    description = "Count the number of registered users"
  )
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

    awsCognitoIdentityProvider.adminCreateUser(registrationRequest);
  }
}
