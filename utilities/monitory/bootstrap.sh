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

aws --endpoint-url $aws_endpoint sqs create-queue --queue-name btg-monitory

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

sleep 2

aws --endpoint-url $aws_endpoint sqs create-queue --queue-name btg-monitory-dlq

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

printf '\nExecution terminated.\n\n'







