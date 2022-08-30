# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name stratospheric-ecs-basic-network \
  --template-body file://network.yml \
  --capabilities CAPABILITY_IAM

aws cloudformation wait stack-create-complete --stack-name stratospheric-ecs-basic-network

aws cloudformation create-stack \
  --stack-name stratospheric-ecs-basic-service \
  --template-body file://service.yml \
  --parameters \
      ParameterKey=StackName,ParameterValue=stratospheric-ecs-basic-network \
      ParameterKey=ServiceName,ParameterValue=stratospheric-todo-app-v1 \
      ParameterKey=ImageUrl,ParameterValue=docker.io/stratospheric/todo-app-v1:latest \
      ParameterKey=ContainerPort,ParameterValue=8080

aws cloudformation wait stack-create-complete --stack-name stratospheric-ecs-basic-service

CLUSTER_NAME=$(aws cloudformation describe-stacks --stack-name stratospheric-ecs-basic-network --output text --query 'Stacks[0].Outputs[?OutputKey==`ClusterName`].OutputValue | [0]')
echo "ECS Cluster:       " $CLUSTER_NAME

TASK_ARN=$(aws ecs list-tasks --cluster $CLUSTER_NAME --output text --query 'taskArns[0]')
echo "ECS Task:          " $TASK_ARN

ENI_ID=$(aws ecs describe-tasks --cluster $CLUSTER_NAME --tasks $TASK_ARN --output text --query 'tasks[0].attachments[0].details[?name==`networkInterfaceId`].value')
echo "Network Interface: " $ENI_ID

PUBLIC_IP=$(aws ec2 describe-network-interfaces --network-interface-ids $ENI_ID --output text --query 'NetworkInterfaces[0].Association.PublicIp')
echo "Public IP:         " $PUBLIC_IP

echo "You can access your service at http://$PUBLIC_IP:8080"
