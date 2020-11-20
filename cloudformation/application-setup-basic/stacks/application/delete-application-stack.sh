#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation delete-stack --stack-name stratospheric-application-parent

aws cloudformation wait stack-delete-complete --stack-name stratospheric-application-parent
