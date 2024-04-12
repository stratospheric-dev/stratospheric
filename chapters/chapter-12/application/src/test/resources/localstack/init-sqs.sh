#!/bin/bash

# This script sets up an SQS queue and a corresponding Dead Letter Queue in LocalStack

# Install jq if it's not already installed
apt-get install jq -y

# Set up names for your main and DLQ queues
DLQ_NAME="stratospheric-todo-sharing-dlq"
QUEUE_NAME="stratospheric-todo-sharing"

# Create the Dead Letter Queue
echo "Creating Dead Letter Queue..."
DLQ_RESULT=$(awslocal sqs create-queue --queue-name $DLQ_NAME)
echo "DLQ created: $DLQ_RESULT"

# Extract the ARN of the Dead Letter Queue
DLQ_ARN=$(echo $DLQ_RESULT | jq -r '.QueueUrl' | awk -F'/' '{print "arn:aws:sqs:eu-central-1:000000000000:"$5}')

if [ -z "$DLQ_ARN" ]; then
  echo "Failed to extract DLQ ARN"
  exit 1
fi

echo "DLQ ARN: $DLQ_ARN"

# Create the attributes JSON for the main queue with the DLQ configured as its dead letter queue
ATTRIBUTES_JSON="{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"$DLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"2\\\"}\",\"MessageRetentionPeriod\":\"259200\"}"

echo "Attributes JSON: $ATTRIBUTES_JSON"

# Create the main queue with the DLQ configured as its dead letter queue
echo "Creating main queue..."
QUEUE_RESULT=$(awslocal sqs create-queue --queue-name $QUEUE_NAME --attributes "$ATTRIBUTES_JSON")
echo "Main queue created: $QUEUE_RESULT"

echo "SQS Setup complete."
