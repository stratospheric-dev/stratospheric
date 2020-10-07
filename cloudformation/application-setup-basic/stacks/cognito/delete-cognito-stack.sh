#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation delete-stack --stack-name aws101-cognito

aws cloudformation wait stack-delete-complete --stack-name aws101-cognito
