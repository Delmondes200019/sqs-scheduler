#\bin\bash

printf 'Validating docker-compose.yml file...\n\n'
docker-compose config

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

printf '\nTerminating services...\n\n'
docker-compose down

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

printf '\nInitializing services...\n\n'
docker-compose up -d

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

aws_endpoint="http://localhost:4566"

sleep 8

aws --endpoint-url $aws_endpoint sqs create-queue --queue-name btg-monitory-create

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

sleep 2

aws --endpoint-url $aws_endpoint sqs create-queue --queue-name btg-monitory-create-dlq

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

sleep 2

aws --endpoint-url $aws_endpoint sqs create-queue --queue-name btg-monitory-ttl-reached

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

sleep 2

aws --endpoint-url $aws_endpoint sqs create-queue --queue-name btg-monitory-sourcesystem-update

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

sleep 2

aws --endpoint-url $aws_endpoint sns create-topic --name btg-monitory

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

sleep 2

aws --endpoint-url $aws_endpoint sns subscribe \
    --topic-arn arn:aws:sns:us-east-1:000000000000:btg-monitory \
    --protocol sqs \
    --notification-endpoint arn:aws:sqs:us-east-1:000000000000:btg-monitory

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

sleep 2

aws --endpoint-url $aws_endpoint dynamodb create-table \
    --table-name monitory \
    --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE\
    --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
    --billing-mode PAY_PER_REQUEST \
    --region us-east-1

sleep 2

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
          "QueueUrl": "https://sqs.us-east-1.amazonaws.com/000000000000/monitory-ttl-reached",
          "MessageBody.$": "$.input.message"
        },
        "End": true
      }
    }
    }' \
    --role-arn "arn:aws:iam::000000000000:role/stepfunctions-role"

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

printf '\nExecution terminated.\n\n'







