package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AwsS3Service {

    private final AmazonS3 s3client;

    public AwsS3Service(final S3ClientFactory factory) {
        s3client = factory.getClient();
    }

    public String createBucket(String bucketName) {
        if (s3client.doesBucketExist(bucketName)) {
            log.error("bucket already exists bucketName='{}'", bucketName);
            return "fail";
        }
        s3client.createBucket(bucketName);
        return "success";
    }

    public String deleteBucket(String bucketName) {
        try {
            s3client.deleteBucket(bucketName);
        } catch (AmazonServiceException e) {
            log.error("failed to delete bucketName='{}'", bucketName, e);
            return "fail";
        }
        return "success";
    }

    public List<String> getBucketKeyList() {
        return s3client.listBuckets()
            .stream()
            .map(Bucket::getName)
            .toList();
    }

    public String upload(String bucketName, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        try {
            s3client.putObject(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata);
        } catch (Exception e) {
            log.error("failed to add object to bucketName='{}'", bucketName, e);
            return "fail";
        }
        return "success";
    }

    public List<String> getObjectKeys(String bucket) {
        List<String> keys = new ArrayList<>();
        ObjectListing objectListing = s3client.listObjects(bucket);
        for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
            keys.add(os.getKey());
        }
        return keys;
    }

    public byte[] getObject(String bucket, String key) {
        try {
            final S3Object s3object = s3client.getObject(bucket, key);
            return s3object.getObjectContent().readAllBytes();
        } catch (Exception e) {
            final String msg = String.format("failed to get object bucketName='%s' key='%s'", bucket, key);
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    public String deleteObject(String bucket, String object) {
        s3client.deleteObject(bucket, object);
        return "success";
    }

}
