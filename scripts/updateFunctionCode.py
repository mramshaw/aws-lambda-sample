# Boto script for invoking: http://docs.aws.amazon.com/cli/latest/reference/lambda/update-function-code.html

import json
import sys

import boto3
import yaml

if len(sys.argv) != 4:
    raise Exception("Invalid Usage")

environment = str(sys.argv[1])
version = str(sys.argv[2])
bucket = str(sys.argv[3])

stream = file('../aws_credentials.yml', 'r')
keys = yaml.load(stream)

if environment == 'production':
    access_key = keys['production_access_key']
    secret_key = keys['production_secret_key']
else:
    access_key = keys['staging_access_key']
    secret_key = keys['staging_secret_key']

session = boto3.Session(aws_access_key_id=access_key,
                        aws_secret_access_key=secret_key,
                        region_name="us-west-2")

client = session.client('lambda')

response = client.update_function_code(
    FunctionName="sampleLambda_" + environment,
    S3Bucket=bucket,
    S3Key="sample_lambda/" + environment + "/sample-lambda-" + version + ".zip",
    Publish=False
)

print json.dumps(response, sort_keys=True, indent=4, separators=(',', ': '))
