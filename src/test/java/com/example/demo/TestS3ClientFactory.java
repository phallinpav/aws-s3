package com.example.demo;

import static org.mockito.Mockito.mock;

import com.amazonaws.services.s3.AmazonS3;

public class TestS3ClientFactory implements S3ClientFactory {

    private final AmazonS3 mockAmazonS3;

    public TestS3ClientFactory() {
        mockAmazonS3 = mock(AmazonS3.class);
    }

    @Override
    public AmazonS3 getClient() {
        return mockAmazonS3;
    }

}
