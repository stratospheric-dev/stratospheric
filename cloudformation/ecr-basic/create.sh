# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

REGISTRY_NAME=stratospheric

aws cloudformation create-stack \
  --stack-name stratospheric-basic-ecr \
  --template-body file://ecr.yml \
  --parameters \
      ParameterKey=RegistryName,ParameterValue=$REGISTRY_NAME \

aws cloudformation wait stack-create-complete --stack-name stratospheric-basic-ecr

REGISTRY_URL=$(aws ecr describe-repositories --repository-names $REGISTRY_NAME --query 'repositories[0].repositoryUri')
echo "ECR Registry URL:       " $REGISTRY_URL
