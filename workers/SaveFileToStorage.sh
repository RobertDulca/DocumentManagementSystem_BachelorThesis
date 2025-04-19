#!/bin/bash

# Define the URI and upload path
uri="http://localhost:8081/subdir1/subdir2/CommunicationPatterns.pdf"

uploadPath="CommunicationPatterns.pdf"

# Define the content type
contentType="application/octet-stream"

# Use curl to make the POST request
response=$(curl -X POST -H "Content-Type: $contentType" --data-binary "@$uploadPath" $uri)

# If you want to print the response, uncomment the line below
echo $response
