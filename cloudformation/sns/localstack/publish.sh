# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

aws sns publish \
  --topic-arn \
  --message "Test" \
  --endpoint-url http://localhost:4566 \
  --profile localstack
