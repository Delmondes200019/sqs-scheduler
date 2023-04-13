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

aws --endpoint-url $aws_endpoint sqs create-queue --queue-name simple-queue

if [ ! $? -eq  0 ]; then
    printf 'Execution failed...'
    exit
fi

printf '\nExecution terminated.\n\n'







