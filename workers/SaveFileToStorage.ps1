# $uri = "http://localhost:8081/CommunicationPatterns.pdf"
$uri = "http://localhost:8081/test/CommunicationPatterns.pdf"

# $uploadPath = "C:\Users\Bernhard\Desktop\CommunicationPatterns.pdf"
$uploadPath = "CommunicationPatterns.pdf"

# Read the file content as a byte array
$fileContent = [System.IO.File]::ReadAllBytes($uploadPath)

# Set the headers and parameters
$headers = @{
    "Content-Type" = "application/octet-stream"
}

# Make the POST request
$response = Invoke-RestMethod -Uri $uri -Method POST -Headers $headers -Body $fileContent

# Process the response if needed
$response


