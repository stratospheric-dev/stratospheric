package dev.stratospheric.todoapp.registration;

import java.time.Duration;
import java.util.List;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
public class CognitoRegistrationService implements RegistrationService {

  private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;
  private final MeterRegistry meterRegistry;
  private final String userPooldId;

  public CognitoRegistrationService(
    @Value("${COGNITO_USER_POOL_ID}") String userPoolId,
    AWSCognitoIdentityProvider awsCognitoIdentityProvider,
    MeterRegistry meterRegistry) {
    this.awsCognitoIdentityProvider = awsCognitoIdentityProvider;
    this.meterRegistry = meterRegistry;
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

    meterRegistry.timer("stratospheric.registration.speed")
      .record(() -> {
        awsCognitoIdentityProvider.adminCreateUser(registrationRequest);
      });

    meterRegistry.timer("stratospheric.registration.speed2")
      .record(Duration.ofSeconds(30));

    Counter successCounter = Counter.builder("stratospheric.registration.signups")
      .description("Number of user registrations")
      .tag("outcome", "success")
      .register(meterRegistry);

    meterRegistry.counter("stratospheric.registration.signup"
        , List.of(new ImmutableTag("outcome", "success")))
      .increment();

    successCounter.increment();
  }
}
