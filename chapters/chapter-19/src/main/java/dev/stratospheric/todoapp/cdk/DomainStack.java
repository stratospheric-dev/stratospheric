package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.certificatemanager.DnsValidatedCertificate;
import software.amazon.awscdk.services.elasticloadbalancing.LoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.*;
import software.amazon.awscdk.services.route53.*;
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
      .subjectAlternativeNames(List.of(applicationDomain))
      .build();

    IApplicationLoadBalancer applicationLoadBalancer = ApplicationLoadBalancer.fromLookup(
      scope,
      "LoadBalancer",
      ApplicationLoadBalancerLookupOptions.builder()
        .loadBalancerArn(networkOutputParameters.getLoadBalancerArn())
        .build()
    );
    ApplicationListener listener = applicationLoadBalancer.addListener(
      "HttpListener",
      BaseApplicationListenerProps.builder()
        .protocol(ApplicationProtocol.HTTP)
        .port(80)
        .build()
    );
    listener.addAction(
      "Redirect",
      AddApplicationActionProps.builder()
        .action(ListenerAction.redirect(
          RedirectOptions.builder()
            .port("443")
            .build()
        ))
        .build()
    );
    ApplicationListenerRule applicationListenerRule = new ApplicationListenerRule(
      this,
      "HttpListenerRule",
      ApplicationListenerRuleProps.builder()
        .listener(listener)
        .priority(1)
        .conditions(
          List.of(
            ListenerCondition.pathPatterns(List.of("*"))
          )
        )
        .build()
    );

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
