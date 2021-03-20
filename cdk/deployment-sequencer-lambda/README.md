# Deployment Sequencer Lambda

This is the source for a Lambda function that makes sure that the latest deployment event will trigger a GitHub Actions workflow.

Events have this shape:

```json
{
  "Records": [
    {
      "messageId": "19dd0b57-b21e-4ac1-bd88-01bbb068cb78",
      "receiptHandle": "MessageReceiptHandle",
      "body": "{\"commitSha\": \"${GITHUB_SHA}\", \"ref\": \"master\", \"owner\": \"blogtrack\", \"repo\": \"blogtrack\", \"workflowId\": \"deploy-to-staging.yml\", \"dockerImageTag\": \"${DOCKER_IMAGE_TAG}\"}",
      "attributes": {
        "ApproximateReceiveCount": "1",
        "SentTimestamp": "1523232000000",
        "SenderId": "123456789012",
        "ApproximateFirstReceiveTimestamp": "1523232000001"
      },
      "messageAttributes": {},
      "md5OfBody": "{{{md5_of_body}}}",
      "eventSource": "aws:sqs",
      "eventSourceARN": "arn:aws:sqs:ap-southeast-2:123456789012:MyQueue",
      "awsRegion": "ap-southeast-2"
    }
  ]
}
```
