#!/usr/bin/env bash
# Using https://ramblingsofasoftwaredevelopermanager.wordpress.com/2019/05/18/a-lighter-way-to-deploy-to-aws-ecs/ as a template
# Usage: ./deploy DOCKER_BUILD_TAG -> ./deploy 10

set -eo pipefail

DOCKER_TAG=$1
DOCKER_IMAGE=$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/aws101:$DOCKER_TAG
ECS_SERVICE_NAME=aws101-todo-app

echo "Deploying a new version of $ECS_SERVICE_NAME with build number $DOCKER_TAG"

ECS_CLUSTER_NAME=$(aws ecs list-clusters --region $AWS_REGION --output text --query 'clusterArns[0]')
ECS_EXECUTION_ROLE=$(aws iam get-role --role-name aws101-application-network-ECSTaskExecutionRole-1DR8RAZ6ZKYQJ --region $AWS_REGION --output text --query 'Role.Arn')
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cat $DIR/ecs_task_template.json | jq --arg img "$DOCKER_IMAGE" --arg role "$ECS_EXECUTION_ROLE" '.containerDefinitions[0].image = $img | .executionRoleArn = $role' > revision.json

REGISTER_RESULT=$(aws ecs register-task-definition --cli-input-json file://./revision.json --region $AWS_REGION)
TASK_ARN=$(echo $REGISTER_RESULT | jq -r .taskDefinition.taskDefinitionArn)

RESULT=$(aws ecs update-service --cluster $ECS_CLUSTER_NAME --service $ECS_SERVICE_NAME --task-definition $TASK_ARN --region $AWS_REGION)

echo "Successfully deployed a new version of $ECS_SERVICE_NAME with build number $DOCKER_TAG"