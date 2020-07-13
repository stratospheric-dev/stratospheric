package dev.aws101.registration;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RegistrationService {

  private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;
  private final String userPooldId;
  private final Set<String> validInvitationCodes;

  public RegistrationService(AWSCognitoIdentityProvider awsCognitoIdentityProvider,
                             @Value("${COGNITO_USER_POOL_ID}") String userPoolId,
                             @Value("${custom.invitationCodes}") Set<String> validInvitationCodes) {
    this.awsCognitoIdentityProvider = awsCognitoIdentityProvider;
    this.userPooldId = userPoolId;
    this.validInvitationCodes = validInvitationCodes;
  }

  public UserType registerUser(Registration registration) {

    // TODO: Catch some error scenarios, e.g. username is already taken see UsernameExistsException

    AdminCreateUserRequest registrationRequest = new AdminCreateUserRequest()
      .withUserPoolId(userPooldId)
      .withUsername(registration.getUsername())
      .withUserAttributes(
        new AttributeType().withName("email").withValue(registration.getEmail()),
        new AttributeType().withName("name").withValue(registration.getUsername()),
        new AttributeType().withName("email_verified").withValue("true"))
      .withTemporaryPassword(registration.getPassword())
      .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
      .withForceAliasCreation(Boolean.FALSE);

    AdminCreateUserResult createUserResult = awsCognitoIdentityProvider.adminCreateUser(registrationRequest);

    return createUserResult.getUser();
  }

  public boolean isValidInvitationCode(String invitationCode) {
    return validInvitationCodes.contains(invitationCode);
  }
}
