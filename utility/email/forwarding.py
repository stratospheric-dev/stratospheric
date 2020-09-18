# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import os
import boto3
import email
import re

from botocore.exceptions import ClientError
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.application import MIMEApplication

region = os.environ['Region']

def get_message_from_s3(message_id):

    incoming_email_bucket = os.environ['MailS3Bucket']
    incoming_email_prefix = os.environ['MailS3Prefix']

    if incoming_email_prefix:
        object_path = (incoming_email_prefix + "/" + message_id)
    else:
        object_path = message_id

    object_http_path = (f"http://s3.console.aws.amazon.com/s3/object/{incoming_email_bucket}/{object_path}?region={region}")

    client_s3 = boto3.client("s3")
    object_s3 = client_s3.get_object(Bucket=incoming_email_bucket,Key=object_path)

    file = object_s3['Body'].read()

    file_dict = {
        "file": file,
        "path": object_http_path
    }

    return file_dict

def create_message(file_dict):

    sender = os.environ['MailSender']
    recipient = os.environ['MailRecipientPhilip'] # TODO: Select forwarding recipient based on original recipient

    separator = ";"

    mailobject = email.message_from_string(file_dict['file'].decode('utf-8'))

    subject_original = mailobject['Subject']
    subject = "FW: " + subject_original

    body_text = ("The attached message was received from "
                 + separator.join(mailobject.get_all('From'))
                 + ". This message is archived at " + file_dict['path'])

    filename = re.sub('[^0-9a-zA-Z]+', '_', subject_original) + ".eml"

    msg = MIMEMultipart()
    text_part = MIMEText(body_text, _subtype="html")
    msg.attach(text_part)

    msg['Subject'] = subject
    msg['From'] = sender
    msg['To'] = recipient

    att = MIMEApplication(file_dict["file"], filename)
    att.add_header("Content-Disposition", 'attachment', filename=filename)

    msg.attach(att)

    message = {
        "Source": sender,
        "Destinations": recipient,
        "Data": msg.as_string()
    }

    return message

def send_email(message):
    aws_region = os.environ['Region']

    client_ses = boto3.client('ses', region)

    try:
        response = client_ses.send_raw_email(
            Source=message['Source'],
            Destinations=[
                message['Destinations']
            ],
            RawMessage={
                'Data':message['Data']
            }
        )

    except ClientError as e:
        output = e.response['Error']['Message']
    else:
        output = "Email sent! Message ID: " + response['MessageId']

    return output

def lambda_handler(event, context):
    message_id = event['Records'][0]['ses']['mail']['messageId']

    print(f"Received message ID {message_id}")

    file_dict = get_message_from_s3(message_id)
    message = create_message(file_dict)
    result = send_email(message)

    print(result)