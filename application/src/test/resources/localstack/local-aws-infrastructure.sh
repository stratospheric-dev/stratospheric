#!/bin/sh

awslocal sqs create-queue --queue-name stratospheric-todo-sharing

awslocal ses verify-email-identity --email-address noreply@stratospheric.dev
awslocal ses verify-email-identity --email-address info@stratospheric.dev
awslocal ses verify-email-identity --email-address tom@stratospheric.dev
awslocal ses verify-email-identity --email-address bjoern@stratospheric.dev
awslocal ses verify-email-identity --email-address philip@stratospheric.dev

awslocal dynamodb create-table \
    --table-name local-todo-app-breadcrumb \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=10 \

awslocal cognito-idp create-user-pool --pool-name local-stratospheric --auto-verified-attributes email --user-pool-tags "_custom_id_=stratospheric"
awslocal cognito-idp create-user-pool-client --user-pool-id stratospheric --client-name local-todo-app --client-tags "_custom_id_=local-todo-app"

awslocal cognito-idp sign-up --client-id stratospheric --username duke --password stratospheric --user-attributes Name=email,Value=duke@stratospheric.dev
awslocal cognito-idp sign-up --client-id stratospheric --username tom --password stratospheric --user-attributes Name=email,Value=tom@stratospheric.dev
awslocal cognito-idp sign-up --client-id stratospheric --username bjoern --password stratospheric --user-attributes Name=email,Value=bjoern@stratospheric.dev
awslocal cognito-idp sign-up --client-id stratospheric --username philip --password stratospheric --user-attributes Name=email,Value=philip@stratospheric.dev

echo "Initialized."
