I would have liked to put the Cognito stack as a nested stack into the application stack, but CloudFormation currently doesn't provide a way to pass a UserPool client secret on to another resource that's part of the same (parent) stack.

Thus, we need to have the Cognito stack up and running, get the secret via AWS CLI, and only then we can create the application stack.