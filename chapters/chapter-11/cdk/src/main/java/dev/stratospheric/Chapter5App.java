package dev.stratospheric;

import software.amazon.awscdk.core.App;

public class Chapter5App {
  public static void main(final String[] args) {
    App app = new App();

    new CdkStack(app, "stratospheric-chapter-5-app");

    app.synth();
  }
}
