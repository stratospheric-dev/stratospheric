package dev.stratospheric;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

import java.util.List;
import java.util.Map;

public class MQStack extends Stack {

  public MQStack(final Construct scope,
                      final String id,
                      final Environment environment,
                      final Map<String, List<String>> clients) {

    super(scope, id, StackProps.builder()
      .stackName("MQCdkTestStack")
      .env(environment)
      .build());

  }
}
