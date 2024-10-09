package com.wd.filemanager.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3ServiceImp {
    @Getter
    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public S3ServiceImp(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFileToS3(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
        return s3Client.getUrl(bucketName, fileName).toString();
    }


}
