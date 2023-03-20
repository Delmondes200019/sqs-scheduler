#\bin\bash

aws_endpoint="http://localhost:4566"

printf '\nCreating StepFunction...\n\n'

#arn:aws:states:us-east-1:000000000000:stateMachine:monitory-ttl

aws --endpoint-url $aws_endpoint stepfunctions delete-state-machine --state-machine-arn arn:aws:states:us-east-1:000000000000:stateMachine:monitory-ttl

sleep 2

aws --endpoint-url $aws_endpoint iam delete-role --role-name step-function-sqs

sleep 2

aws --endpoint-url $aws_endpoint iam create-role --role-name step-function-sqs --assume-role-policy-document \ '{
  "Version": "2012-10-17",
  "Id": "Queue1_Policy_UUID",
  "Statement": [
    {
        "Sid":"SFNSendMessageToQueue",
        "Effect": "Allow",
        "Principal": "*",
        "Action": "sqs:SendMessage",
        "Resource": "arn:aws:sqs:*:000000000000:*"
      }
    ]
  }'


aws --endpoint-url $aws_endpoint stepfunctions create-state-machine \
  --name "monitory-ttl" \
  --definition '{
    "StartAt": "WaitExecution",
    "States": {
      "WaitExecution": {
        "Type": "Wait",
        "Seconds": 20,
        "Next": "SendToMonitoryTtlReachedSqs"
      },
      "SendToMonitoryTtlReachedSqs": {
        "Type": "Task",
        "Resource": "arn:aws:states:::sqs:sendMessage",
        "Parameters": {
          "QueueUrl": "http://localhost:4566/monitory-ttl-reached",
          "MessageBody.$": "$"
        },
        "End": true
      }
    }
    }' \
    --role-arn "arn:aws:iam::000000000000:role/step-function-sqs"
