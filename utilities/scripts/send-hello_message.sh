#\bin\bash

aws_endpoint="http://localhost:4566"

printf '\nSending message to SQS...\n\n'

aws --endpoint-url $aws_endpoint sqs send-message --queue-url "http://localhost:4566/000000000000/simple-queue" \
  --message-body "{\"message\": \"Hello World\"}"