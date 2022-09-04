package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;
import software.constructs.Construct;

public class CdkStack extends Stack {
  public CdkStack(final Construct scope, final String id) {
    this(scope, id, null);
  }

  public CdkStack(final Construct scope, final String id, final StackProps props) {
    super(scope, id, props);

    new Vpc(this, "vpc", VpcProps.builder()
      .cidr("10.0.0.0/24")
      .build());
  }
}
