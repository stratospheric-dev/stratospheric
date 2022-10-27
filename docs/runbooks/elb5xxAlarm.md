# Elb5xxAlarm

## Meaning

The users and clients of the Stratospheric Todo application experience a high failure rate of their HTTP responses as the load balancer returns 5xx server errors.

## Impact

Users are unable to perform tasks within the application.

Clients and other dependent systems are unable to communicate with the Todo application to synchronize the state of a user's todos.

## Diagnosis

1. Check the [CloudWatch logs](#) for errors and warnings
2. Check the [operational dashboard](#) for any anomaly in the infrastructure
3. Verify in the [#platform](#) channel that there's currently no ongoing platform incident or a maintenance window
4. Log in to the [administration backend](#) to see if

## Mitigation

### HTTP 502 and 503

The application may be in a starting loop. Verify this by checking the uptime and events for the ECS tasks

### Database is unhealthy

1. Ensure proper connection to the database
2. Go through Postgres logs for detailed information
3. Add more disk space to your Postgres cluster
4. Fix errors to make sure database that is able to accept connections
5. Restart the database the `./restart-db.sh` script

### NullPointerExceptions

Programming error, no . Get in touc with the developers, track the latest release log and identifyi f there have been recent commit and deployments.

### The payment provider is unavailable

In case the payment provider is unavailable, log in to the administration overview and disable the feature to pay by card. The application will fall back to bank transfer payments.

### ...
