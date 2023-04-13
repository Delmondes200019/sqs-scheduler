#\bin\bash

aws_endpoint="http://localhost:4566"

printf '\nPurging Queue...\n\n'

aws --endpoint-url $aws_endpoint sqs purge-queue --queue-url http://localhost:4566/000000000000/simple-queue