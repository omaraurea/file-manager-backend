package com.wd.filemanager.controller;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.wd.filemanager.model.FileDb;
import com.wd.filemanager.service.DynamoDBServiceImp;
import com.wd.filemanager.service.S3ServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileManagerController {

    private final S3ServiceImp s3Service;
    private final DynamoDBServiceImp dynamoDBService;
    private final AmazonS3 amazonS3;

    @Autowired
    public FileManagerController(S3ServiceImp s3Service, DynamoDBServiceImp dynamoDBService, AmazonS3 amazonS3) {
        this.s3Service = s3Service;
        this.dynamoDBService = dynamoDBService;
        this.amazonS3 = amazonS3;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("user") String user) {
        try {

            String s3Url = s3Service.uploadFileToS3(file);
            List<FileDb> fileRecords = dynamoDBService.findFilebyNameAndUser(file.getOriginalFilename(), user);

            String successMessage;

            if (!fileRecords.isEmpty()) {
                dynamoDBService.updateFileRecord(file.getOriginalFilename(), user, s3Url, fileRecords);
                successMessage = "Successfully updated " + file.getOriginalFilename();
            } else {
                dynamoDBService.saveFileRecord( file.getOriginalFilename(), s3Url, user);
                successMessage = "Successfully saved " + file.getOriginalFilename();
            }
            return ResponseEntity.ok(successMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/user/{user}")
    public ResponseEntity<List<FileDb>> getFilesByUser(@PathVariable("user") String user) {
        List<FileDb> files = dynamoDBService.loadFilesByUser(user);
        return ResponseEntity.ok(files);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String id, @RequestParam String user) {
        try {
            dynamoDBService.deleteFile(id, user);
            return ResponseEntity.ok("File deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete file: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String id, @RequestParam String user) throws IOException {

        S3Object s3Object = dynamoDBService.getS3Object(id, user,s3Service.getBucketName());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Object.getKey() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }

}
