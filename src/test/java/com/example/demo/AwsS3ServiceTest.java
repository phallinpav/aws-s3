package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

class AwsS3ServiceTest {

    private static final String TEST_BUCKET_NAME = "cow_moo";
    private static final String TEST_FILENAME = "dave.txt";

    private AmazonS3 mockAmazonS3;
    private AwsS3Service service;

    @BeforeEach
    void setup() {
        final S3ClientFactory factory = new TestS3ClientFactory();
        mockAmazonS3 = factory.getClient();
        service = new AwsS3Service(factory);
    }

    @Test
    void createBucket_alreadyExists() {
        when(mockAmazonS3.doesBucketExist(TEST_BUCKET_NAME)).thenReturn(true);

        assertEquals("fail", service.createBucket(TEST_BUCKET_NAME));

        verify(mockAmazonS3, never()).createBucket(anyString());
    }

    @Test
    void createBucket_doesNotExist() {
        when(mockAmazonS3.doesBucketExist(TEST_BUCKET_NAME)).thenReturn(false);

        assertEquals("success", service.createBucket(TEST_BUCKET_NAME));

        verify(mockAmazonS3).createBucket(anyString());
    }

    @Test
    void deleteBucket_exception() {
        doThrow(new AmazonServiceException("TEST")).when(mockAmazonS3).deleteBucket(TEST_BUCKET_NAME);

        assertEquals("fail", service.deleteBucket(TEST_BUCKET_NAME));
    }

    @Test
    void deleteBucket_noProblem() {
        assertEquals("success", service.deleteBucket(TEST_BUCKET_NAME));
    }

    @Test
    void bucketList() {
        final Bucket b1 = new Bucket();
        b1.setName("cow");
        final Bucket b2 = new Bucket();
        b2.setName("moon");
        when(mockAmazonS3.listBuckets()).thenReturn(List.of(b1, b2));

        final List<String> names = service.getBucketKeyList();

        assertEquals(2, names.size());
        assertEquals("cow", names.get(0));
        assertEquals("moon", names.get(1));
    }

    @Test
    void addObject_fails() {
        when(mockAmazonS3.putObject(eq(TEST_BUCKET_NAME), any(), any(), any())).thenThrow(new AmazonServiceException("TEST"));
        final MultipartFile file = mock(MultipartFile.class);

        final String result = service.upload(TEST_BUCKET_NAME, file);

        assertEquals("fail", result);
    }

    @Test
    void addObject_ok() {
        final MultipartFile file = mock(MultipartFile.class);

        final String result = service.upload(TEST_BUCKET_NAME, file);

        assertEquals("success", result);
    }

    @Test
    void getObjectKeys() {
        final ObjectListing ol = mock(ObjectListing.class);
        when(mockAmazonS3.listObjects(TEST_BUCKET_NAME)).thenReturn(ol);

        final List<S3ObjectSummary> summaries = new ArrayList<>();
        when(ol.getObjectSummaries()).thenReturn(summaries);

        final S3ObjectSummary os1 = new S3ObjectSummary();
        os1.setKey("cow");
        summaries.add(os1);

        final S3ObjectSummary os2 = new S3ObjectSummary();
        os2.setKey("moon");
        summaries.add(os2);

        final List<String> keys = service.getObjectKeys(TEST_BUCKET_NAME);

        assertEquals(2, keys.size());
        assertEquals("cow", keys.get(0));
        assertEquals("moon", keys.get(1));
    }

    @Test
    void getObject_getObjectFails() {
        when(mockAmazonS3.getObject(anyString(), anyString())).thenThrow(new AmazonServiceException("TEST"));

        assertThrows(RuntimeException.class, () -> service.getObject(TEST_BUCKET_NAME, TEST_FILENAME));
    }

    @Test
    void getObject_getContentFails() {
        final S3Object obj = mock(S3Object.class);
        when(obj.getObjectContent()).thenThrow(new AmazonServiceException("TEST"));
        when(mockAmazonS3.getObject(anyString(), anyString())).thenReturn(obj);

        assertThrows(RuntimeException.class, () -> service.getObject(TEST_BUCKET_NAME, TEST_FILENAME));
    }

    @Test
    void getObject_ok() throws IOException {
        final S3ObjectInputStream stream = mock(S3ObjectInputStream.class);
        when(stream.readAllBytes()).thenReturn(new byte[1]);
        final S3Object obj = mock(S3Object.class);
        when(obj.getObjectContent()).thenReturn(stream);
        when(mockAmazonS3.getObject(TEST_BUCKET_NAME, TEST_FILENAME)).thenReturn(obj);

        final byte[] result = service.getObject(TEST_BUCKET_NAME, TEST_FILENAME);

        assertNotNull(result);
    }

    @Test
    void deleteObject() {
        assertEquals("success", service.deleteObject(TEST_BUCKET_NAME, TEST_FILENAME));
    }

}
