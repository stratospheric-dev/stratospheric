export AWS_PAGER=""

# create the parent stack with all child stacks
aws cloudformation create-stack \
  --stack-name aws101-container-registry \
  --template-body file://stacks/registry/registry.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=RegistryName,ParameterValue=aws101

# wait for stack to be created
aws cloudformation wait stack-create-complete --stack-name aws101-container-registry
