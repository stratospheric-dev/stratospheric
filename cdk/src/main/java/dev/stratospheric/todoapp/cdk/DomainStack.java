package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.certificatemanager.DnsValidatedCertificate;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancerLookupOptions;
import software.amazon.awscdk.services.route53.*;
import software.amazon.awscdk.services.route53.patterns.HttpsRedirect;
import software.amazon.awscdk.services.route53.targets.LoadBalancerTarget;

import java.util.List;

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

    Network.NetworkOutputParameters networkOutputParameters =
      Network.getOutputParametersFromParameterStore(this, applicationEnvironment.getEnvironmentName());

    IHostedZone hostedZone = HostedZone.fromLookup(scope, "HostedZone", HostedZoneProviderProps.builder()
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
      .target(
        RecordTarget.fromAlias(
          new LoadBalancerTarget(
            ApplicationLoadBalancer.fromLookup(
              scope,
              "LoadBalancer",
              ApplicationLoadBalancerLookupOptions.builder()
                .loadBalancerArn(networkOutputParameters.getLoadBalancerArn())
                .build()
            )
          )
        )
      ).build();

    applicationEnvironment.tag(this);
  }
}
