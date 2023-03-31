# AWS S3 + Spring Boot

This example show how to use AWS S3 service with java and spring boot web to receive request api request and do action of AWS S3.

## Required

- Java 17 (or higher) from [https://adoptium.net/](https://adoptium.net/)

- Maven 3.6.3 (or higher) from [https://maven.apache.org/](https://maven.apache.org/)

## Build

``mvn``

Note that the first build may take some time due to maven downloading dependencies and tools.

## Run

- from IDE: run ``AwsS3Application`` as a Spring Boot App

- from command line: ``java -jar target/aws-s3-0.0.1-SNAPSHOT.jar``

The application will start and wait to receive http request to perform action accordingly

## AWS S3

AWS S3 is storage service from Amazon AWS. S3 = Simple Storage Service

In order to use AWS s3 you need to have AWS account.

AWS s3 is free with **5GB** storage with 2000 PUT, POST, COPY or LIST requests, 20,000 GET requests and 15 GB of outgoing data transfer per month for a year.
Increase in storage will charge more price accordingly.

In this example project, in application.properties there is accessKey and secretKey available that will have access to your AWS account

```
AWS.s3.accessKey=AKIATHCJxxxxxxxxx
AWS.s3.secretKey=D4MAWMqTqKFw0YMq8Rakxxxxxxxxxxxxxxx
```

To create AWS Account you will require to input your credit card as well. But don't worry there will be no payment spend unless you exceed the free storage that it was given. [Registration Portal](https://portal.AWS.Amazon.com/gp/AWS/developer/registration/index.html)

So you need to create account and generate access key to use

- Once register and login
- Under your account name at top right menu > Security credentials > Access keys (access key ID and secret access key)
- Create new access key
- Copy access key and secret key into application.properties
- And now you can use AWS S3 service using your AWS account

## Request API

### Buckets

- **Create bucket**
    - POST /buckets
    - example: `curl -X POST 'http://localhost:8080/buckets' -H 'Content-Type: text/plain' --data-raw 'mangobyte-bucket-1'`
   
```
  Bucket name must be unique across all existing bucket names in Amazon S3
    names should not contain underscores
    names should be between 3 and 63 characters long
    names should not end with a dash
    names cannot contain adjacent periods
    names cannot contain dashes next to periods (e.g., “my-.bucket.com” and “my.-bucket” are invalid)
    names cannot contain uppercase characters
```
  
- **Get list of existing buckets name**
    - GET /buckets
    - `curl -X GET 'http://localhost:8080/buckets'`


- **Delete bucket**
    - DELETE /buckets
    - `curl -X DELETE 'http://localhost:8080/buckets' -H 'Content-Type: text/plain' --data-raw 'mangobyte-bucket-1'`


### Objects within a bucket

- **Get list of existing object in bucket**
  - GET /buckets/{bucket}/objects
  - `curl -X GET 'http://localhost:8080/buckets/phallin-bucket/objects'`


- **Upload object/file into bucket**
    - POST /buckets/{bucket-name}/objects
    - `curl -X POST 'http://localhost:8080/buckets/phallin-bucket/objects' -F 'file=@"/C:/Users/User/Documents/picture.jpg"'`


- **Download/get object from bucket**
    - GET /buckets/{bucket-name}/objects/{object-name}
    - `curl -X GET 'http://localhost:8080/buckets/phallin-bucket/objects/picture.jpg'`


- **Delete object from bucket**
    - DELETE /buckets/{bucket-name}/objects/{object-name}
    - `curl -X DELETE 'http://localhost:8080/buckets/phallin-bucket/objects/picture.jpg'`
    