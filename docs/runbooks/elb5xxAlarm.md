# Elb5xxAlarm

This is a sample runbook for one of our alarms to provide a basic template.

## Meaning

The users and clients of the Stratospheric Todo application experience a high failure rate of their HTTP responses as the load balancer returns 5xx server errors.

## Impact

Users are unable to perform tasks within the application.

Clients and other dependent systems are unable to communicate with the Todo application to synchronize the state of a user's todos.

## Diagnosis

(0.) Verify in the [#platform](#) channel that there's currently no ongoing platform incident or a maintenance window

1. Check the [CloudWatch logs](#) for errors and warnings
2. Check the [operational dashboard](#) to detect any anomaly in the infrastructure
3. Try to log in to the [administration backend](#) to see if the service is reachable

## Mitigation

### HTTP 502 and 503 Errors

The load balancer fails to connect to our service. The application may be in an endless starting loop. Verify this by checking the uptime duration for the ECS tasks.

The logs should output the reason for the failing startup attempts. Ensure that all credentials are present in the AWS Systems manager. Trigger a credentials rotation and wait until the next container startup attempt.

### Database is unhealthy

1. Ensure there are active connections to the database by checking the [database dashboard](#)
2. Verify the CPU, disk space and memory usage of the database cluster
3. Resize the database to the next larger instance
4. Restart the database using the `./restart-db.sh` script

### The payment provider is unavailable

In case the payment provider is unavailable, log in to the administration overview and disable the feature to pay by card. The application will fall back to bank transfer payments.

### NullPointerExceptions

This is usually due to a programming error. Get in touch with the developers, track the latest release log and identify if there has been a recent deployment.

Revert the recent deployment and re-deploy the last Docker image using our [deployment hub](#).


### ...
