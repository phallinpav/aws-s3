package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductionS3ClientFactory implements S3ClientFactory {

    private final AmazonS3 s3client;

    public ProductionS3ClientFactory(
        @Value("${aws.s3.accessKey}") String accessKey,
        @Value("${aws.s3.secretKey}") String secretKey) {
        log.info("creating S3 client accessKey={}", accessKey);

        final AWSCredentials credentials = new BasicAWSCredentials(
            accessKey,
            secretKey);
        s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_2)
            .build();
    }

    @Override
    public AmazonS3 getClient() {
        return s3client;
    }

}
