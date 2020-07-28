package dev.aws101.registration;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RegistrationService {

  private AWSCognitoIdentityProvider awsCognitoIdentityProvider;
  private final String userPooldId;
  private final Set<String> validInvitationCodes;

  @Autowired
  public RegistrationService(@Value("${COGNITO_USER_POOL_ID:empty}") String userPoolId,
                             @Value("${custom.invitationCodes:none}") Set<String> validInvitationCodes) {
    this.userPooldId = userPoolId;
    this.validInvitationCodes = validInvitationCodes;
  }

  @Autowired(required = false)
  public void setAwsCognitoIdentityProvider(AWSCognitoIdentityProvider awsCognitoIdentityProvider) {
    this.awsCognitoIdentityProvider = awsCognitoIdentityProvider;
  }

  public UserType registerUser(Registration registration) {
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
