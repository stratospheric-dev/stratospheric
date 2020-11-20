#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation delete-stack --stack-name stratospheric-container-registry

aws cloudformation wait stack-delete-complete --stack-name stratospheric-container-registry
