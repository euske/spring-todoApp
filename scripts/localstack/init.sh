#!/bin/bash
set -x
AWSCLI=${AWSCLI:-awslocal}

$AWSCLI dynamodb create-table \
  --region ap-northeast-1 \
  --table-name todo_dynamo_entity \
  --attribute-definitions \
    AttributeName=Id,AttributeType=S \
  --key-schema \
    AttributeName=Id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST

$AWSCLI dynamodb put-item \
  --region ap-northeast-1 \
  --table-name todo_dynamo_entity \
  --item '{"Id":{"S":"e3220b97-3be6-40b0-b814-9ebcab06c7fe"}, "Text":{"S":"foo"}}'
$AWSCLI dynamodb put-item \
  --region ap-northeast-1 \
  --table-name todo_dynamo_entity \
  --item '{"Id":{"S":"75c431bf-e5ec-4253-8d96-e9bb2c6caf8e"}, "Text":{"S":"bar"}}'
