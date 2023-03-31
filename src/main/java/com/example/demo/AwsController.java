package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class AwsController {

    @Autowired
    private AwsS3Service awsService;

    @PostMapping("/buckets")
    public String createBucket(@RequestBody String name) {
        log.info("createBucket name='{}'", name);
        return awsService.createBucket(name);
    }

    @GetMapping("/buckets")
    public List<String> bucketList() {
        log.info("getting list of buckets");
        return awsService.getBucketKeyList();
    }

    @DeleteMapping("/buckets")
    public String deleteBucket(@RequestBody String name) {
        log.info("deleteBucket name='{}'", name);
        return awsService.deleteBucket(name);
    }

    @PostMapping("/buckets/{bucketName}/objects")
    public String upload(@PathVariable String bucketName, @RequestParam MultipartFile file) {
        log.info("adding object to bucketName='{}'", bucketName);
        return awsService.upload(bucketName, file);
    }

    @GetMapping("/buckets/{bucketName}/objects")
    public List<String> getObjectLists(@PathVariable String bucketName) {
        log.info("getting list of objects in bucketName='{}'", bucketName);
        return awsService.getObjectKeys(bucketName);
    }

    @GetMapping(value = "/buckets/{bucketName}/objects/{key}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getKeyObject(@PathVariable String bucketName, @PathVariable String key) {
        log.info("getting object from bucketName='{}' key='{}'", bucketName, key);
        return awsService.getObject(bucketName, key);
    }

    @DeleteMapping("/buckets/{bucketName}/objects/{key}")
    public String deleteObject(@PathVariable String bucketName, @PathVariable String key) {
        log.info("deleting object from bucketName='{}' key='{}'", bucketName, key);
        return awsService.deleteObject(bucketName, key);
    }

}
