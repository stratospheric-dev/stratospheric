package dev.stratospheric.todoapp.cdk;

import java.util.Map;

import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.events.targets.LambdaFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.sqs.IQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

import static java.util.Collections.singletonList;

class DeploymentSequencerStack extends Stack {

	private final IQueue deploymentsQueue;
	private final LambdaFunction deploymentsLambda;

	public DeploymentSequencerStack(
		final Construct scope,
		final String id,
		final Environment awsEnvironment,
		final String applicationName,
		final String githubToken) {
		super(scope, id, StackProps.builder()
			.stackName(applicationName + "-Deployments")
			.env(awsEnvironment).build());

		this.deploymentsQueue = Queue.Builder.create(this, "deploymentsQueue")
			.queueName(applicationName + "-deploymentsQueue.fifo")
			.fifo(true)
			.build();

		SqsEventSource eventSource = SqsEventSource.Builder.create(deploymentsQueue)
			.build();

		this.deploymentsLambda = LambdaFunction.Builder.create(new Function(
			this,
			"deploymentSequencerFunction",
			FunctionProps.builder()
				.code(Code.fromAsset("./deployment-sequencer-lambda/dist/lambda.zip"))
				.runtime(Runtime.NODEJS_12_X)
				.handler("index.handler")
        .logRetention(RetentionDays.TWO_WEEKS)
				.reservedConcurrentExecutions(1)
				.events(singletonList(eventSource))
				.environment(Map.of(
					"GITHUB_TOKEN", githubToken,
					"QUEUE_URL", deploymentsQueue.getQueueUrl(),
					"REGION", awsEnvironment.getRegion()
				)).build()
		)).build();

	}

}
