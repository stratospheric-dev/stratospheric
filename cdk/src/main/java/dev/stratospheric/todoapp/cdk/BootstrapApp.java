package dev.stratospheric.todoapp.cdk;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

/**
 * This is an empty app that we can use for bootstrapping the CDK with the "cdk bootstrap" command.
 * We could do this with other apps, but this would require us to enter all the parameters
 * for that app, which is uncool.
 */
public class BootstrapApp {

  public static void main(final String[] args) {
    App app = new App();

    Stack bootstrapStack = new Stack(app, "Bootstrap", StackProps.builder()
      .build());

    app.synth();
  }

}
