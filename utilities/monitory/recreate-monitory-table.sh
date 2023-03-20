#\bin\bash

aws_endpoint="http://localhost:4566"

printf '\nRecreating monitory table...\n\n'

aws --endpoint-url $aws_endpoint dynamodb delete-table --table-name monitory

sleep 2

aws --endpoint-url $aws_endpoint dynamodb create-table \
    --table-name monitory \
    --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE\
    --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
    --billing-mode PAY_PER_REQUEST \
    --region us-east-1