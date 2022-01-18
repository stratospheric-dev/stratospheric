package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancerAttributes;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationLoadBalancer;
import software.amazon.awscdk.services.route53.ARecord;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.amazon.awscdk.services.route53.RecordTarget;
import software.amazon.awscdk.services.route53.targets.LoadBalancerTarget;
import software.constructs.Construct;

class DomainStack extends Stack {

  public DomainStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final String hostedZoneDomain,
    final String applicationDomain) {
    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Domain"))
      .env(awsEnvironment).build());

    IHostedZone hostedZone = HostedZone.fromLookup(this, "HostedZone", HostedZoneProviderProps.builder()
      .domainName(hostedZoneDomain)
      .build());

    Network.NetworkOutputParameters networkOutputParameters = Network.getOutputParametersFromParameterStore(this, applicationEnvironment.getEnvironmentName());

    IApplicationLoadBalancer applicationLoadBalancer = ApplicationLoadBalancer.fromApplicationLoadBalancerAttributes(
      this,
      "LoadBalancer",
      ApplicationLoadBalancerAttributes.builder()
        .loadBalancerArn(networkOutputParameters.getLoadBalancerArn())
        .securityGroupId(networkOutputParameters.getLoadbalancerSecurityGroupId())
        .loadBalancerCanonicalHostedZoneId(networkOutputParameters.getLoadBalancerCanonicalHostedZoneId())
        .loadBalancerDnsName(networkOutputParameters.getLoadBalancerDnsName())
        .build()
    );

    ARecord aRecord = ARecord.Builder.create(this, "ARecord")
      .recordName(applicationDomain)
      .zone(hostedZone)
      .target(RecordTarget.fromAlias(new LoadBalancerTarget(applicationLoadBalancer)))
      .build();

    applicationEnvironment.tag(this);
  }
}
