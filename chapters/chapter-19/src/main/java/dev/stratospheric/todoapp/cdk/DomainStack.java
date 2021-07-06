package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.core.*;

class DomainStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;

  public DomainStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final String applicationDomain) {
    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Domain"))
      .env(awsEnvironment).build());

    this.applicationEnvironment = applicationEnvironment;

    IHostedZone hostedZone = HostedZone.fromLookup(this, "HostedZone", HostedZoneProviderProps.builder()
      .domainName(applicationDomain)
      .build());

    DnsValidatedCertificate websiteCertificate = DnsValidatedCertificate.Builder.create(this, "WebsiteCertificate")
      .hostedZone(hostedZone)
      .region(awsEnvironment.getRegion())
      .domainName(applicationDomain)
      .subjectAlternativeNames(List.of(String.format("www.%s", applicationDomain)))
      .build();

    HttpsRedirect webHttpsRedirect = HttpsRedirect.Builder.create(this, "WebHttpsRedirect")
      .certificate(websiteCertificate)
      .recordNames(List.of(String.format("www.%s", applicationDomain)))
      .targetDomain(applicationDomain)
      .zone(hostedZone)
      .build();


    ARecord aRecord = ARecord.Builder.create(this, "ARecord")
      .recordName(applicationDomain)
      .zone(hostedZone)
      .target(RecordTarget.fromAlias(// TODO: Retrieve target))
      .build();

    applicationEnvironment.tag(this);
  }
}
