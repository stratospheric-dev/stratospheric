# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name aws101-basic-ecs-network \
  --template-body file://network.yml \
  --capabilities CAPABILITY_IAM

aws cloudformation wait stack-create-complete --stack-name aws101-basic-ecs-network

aws cloudformation create-stack \
  --stack-name aws101-basic-ecs-service \
  --template-body file://service.yml \
  --parameters \
      ParameterKey=StackName,ParameterValue=aws101-basic-ecs-network \
      ParameterKey=ServiceName,ParameterValue=reflectoring-hello-world \
      ParameterKey=ImageUrl,ParameterValue=docker.io/reflectoring/aws-hello-world:latest \
      ParameterKey=ContainerPort,ParameterValue=8080

aws cloudformation wait stack-create-complete --stack-name aws101-basic-ecs-service

CLUSTER_NAME=$(aws cloudformation describe-stacks --stack-name aws101-basic-ecs-network --output text --query 'Stacks[0].Outputs[?OutputKey==`ClusterName`].OutputValue | [0]')
echo "ECS Cluster:       " $CLUSTER_NAME

TASK_ARN=$(aws ecs list-tasks --cluster $CLUSTER_NAME --output text --query 'taskArns[0]')
echo "ECS Task:          " $TASK_ARN

ENI_ID=$(aws ecs describe-tasks --cluster $CLUSTER_NAME --tasks $TASK_ARN --output text --query 'tasks[0].attachments[0].details[?name==`networkInterfaceId`].value')
echo "Network Interface: " $ENI_ID

PUBLIC_IP=$(aws ec2 describe-network-interfaces --network-interface-ids $ENI_ID --output text --query 'NetworkInterfaces[0].Association.PublicIp')
echo "Public IP:         " $PUBLIC_IP

echo "You can access your service at http://$PUBLIC_IP:8080"