#!/bin/sh

awslocal sns create-topic --name stratospheric-todo-sharing
awslocal sqs create-queue --queue-name stratospheric-todo-updates
