package com.example.demo;

import com.amazonaws.services.s3.AmazonS3;

public interface S3ClientFactory {

    AmazonS3 getClient();

}
