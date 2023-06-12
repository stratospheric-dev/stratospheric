package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.certificatemanager.ICertificate;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.constructs.Construct;

public class CertificateStack extends Stack {

  public CertificateStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final String applicationDomain,
    final String hostedZoneDomain) {
    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Certificate"))
      .env(awsEnvironment).build());

    IHostedZone hostedZone = HostedZone.fromLookup(this, "HostedZone", HostedZoneProviderProps.builder()
      .domainName(hostedZoneDomain)
      .build());

    ICertificate websiteCertificate = Certificate.Builder.create(this, "WebsiteCertificate")
      .domainName(applicationDomain)
      .validation(CertificateValidation.fromDns(hostedZone))
      .build();

    new CfnOutput(this, "sslCertificateArn", CfnOutputProps.builder()
      .exportName("sslCertificateArn")
      .value(websiteCertificate.getCertificateArn())
      .build());
  }
}
