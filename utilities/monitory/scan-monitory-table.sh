#\bin\bash

aws_endpoint="http://localhost:4566"

printf '\nRunning scan on monitory table...\n\n'

aws --endpoint-url $aws_endpoint dynamodb scan --table-name monitory