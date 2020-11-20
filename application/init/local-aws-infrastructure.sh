#!/bin/sh

awslocal sns create-topic --name stratospheric-todo-updates
awslocal sqs create-queue --queue-name stratospheric-todo-sharing
