# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

[ $# == 1 ] || exit
TODO_UPDATES_ARN=$1

aws sns publish \
  --topic-arn $TODO_UPDATES_ARN \
  --message "Test" \
  --endpoint-url http://localhost:4566 \
  --profile localstack
