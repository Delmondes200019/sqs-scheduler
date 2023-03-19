#\bin\bash

aws_endpoint="http://localhost:4566"

printf '\nSending message to SNS...\n\n'

aws --endpoint-url $aws_endpoint sns publish \
     --message "{\"tenantCode\":\"1\", \"umbrellaCode\": \"DADOS_CADASTRAIS\", \"identifierCode\":\"123456\", \
      \"requesters\": [ { \"sourceSystem\": \"OPIN_CUSTOMER_REGISTERDATA_LESSWORKER\" }, { \"sourceSystem\": \"OPIN_CUSTOMER_USERDATA_LESSWORKER\" } ] }" \
    --topic-arn arn:aws:sns:us-east-1:000000000000:btg-monitory