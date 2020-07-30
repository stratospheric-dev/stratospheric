package dev.aws101.registration;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        new AttributeType().withName("name").withValue(registration.getUsername())
      )
      .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
      .withForceAliasCreation(Boolean.FALSE);

    AdminCreateUserResult createUserResult;
    // Handle registration in local (i.e. non-AWS) development environment
    if (awsCognitoIdentityProvider != null) {
      createUserResult = awsCognitoIdentityProvider.adminCreateUser(registrationRequest);
    } else {
      createUserResult = new AdminCreateUserResult();

      UserType userType = new UserType();
      userType.setUsername("Admin");
      List<AttributeType> attributeTypeList = new ArrayList<>();
      AttributeType emailAttributeType = new AttributeType();
      emailAttributeType.setName("email");
      emailAttributeType.setValue("admin@aws101.dev");
      attributeTypeList.add(emailAttributeType);
      userType.setAttributes(attributeTypeList);

      createUserResult.setUser(userType);
    }

    return createUserResult.getUser();
  }

  public boolean isValidInvitationCode(String invitationCode) {
    return validInvitationCodes.contains(invitationCode);
  }
}
