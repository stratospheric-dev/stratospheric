# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

aws sns create-topic \
  --endpoint-url http://localhost:4566 \
  --profile localstack \
  --name stratospheric-todo-updates
