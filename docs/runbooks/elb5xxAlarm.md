# Elb5xxAlarm

## Meaning

The users and clients of the Stratospheric Todo application experience a high failure rate of their HTTP responses as the load balancer returns 5xx server errors.

## Impact

Users are unable to perform tasks within the application.

Clients and other dependent systems are unable to communicate with the Todo application to synchronize the state of a user's todos.

## Diagnosis

1. Check the CloudWatch logs for errors.
2. Check the operational dashboard for
4. Check
5. If timeout or 502 or 503 may a wrong load balancer. 500 is from the server
6. Check the availablilty

## Mitigation

### NullPointerExceptions

Reconfigure your data source (Prometheus, Opentelemetry Collector, etc.) to ensure that all applied configurations
are as specified in the documentation

### Downstream System are unavailable

### Database is unhealthy
1. Ensure proper connection to the database
2. Go through Postgres logs for detailed information
3. Add more disk space to your Postgres cluster
4. Fix errors to make sure database that is able to accept connections
