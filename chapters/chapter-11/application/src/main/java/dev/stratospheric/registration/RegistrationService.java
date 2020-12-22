package dev.stratospheric.registration;

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

  public RegistrationService(@Value("${COGNITO_USER_POOL_ID:empty}") String userPoolId) {
    this.userPooldId = userPoolId;
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
        new AttributeType().withName("email_verified").withValue("true")
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
      userType.setUsername(registration.getUsername());
      List<AttributeType> attributeTypeList = new ArrayList<>();
      AttributeType emailAttribute = new AttributeType();
      emailAttribute.setName("email");
      emailAttribute.setValue(registration.getEmail());
      attributeTypeList.add(emailAttribute);
      AttributeType nameAttribute = new AttributeType();
      nameAttribute.setName("name");
      nameAttribute.setValue(registration.getUsername());
      attributeTypeList.add(nameAttribute);
      AttributeType emailVerifiedAttribute = new AttributeType();
      emailVerifiedAttribute.setName("email_verified");
      emailVerifiedAttribute.setValue("true");
      attributeTypeList.add(emailVerifiedAttribute);
      userType.setAttributes(attributeTypeList);

      createUserResult.setUser(userType);
    }

    return createUserResult.getUser();
  }
}
