package com.wd.filemanager.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.wd.filemanager.model.FileDb;
import org.joda.time.Instant;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DynamoDBServiceImp {
    private final DynamoDBMapper dynamoDBMapper;

    private final AmazonS3 amazonS3;

    public DynamoDBServiceImp(DynamoDBMapper dynamoDBMapper, AmazonS3 amazonS3) {
   //        this.fileRepository = fileRepository;
        this.amazonS3 = amazonS3;
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void saveFileRecord(String fileName, String s3Url, String user) {
//        Optional<FileDb> fil =  fileRepository.findById("1");

        FileDb file = new FileDb();
       // file.setId("34");
        file.setUser(user);
        file.setFileName(fileName);
        file.setS3Url(s3Url);
        file.setUploadTimestamp(Instant.now().toString());

        dynamoDBMapper.save(file);
    }

    public List<FileDb> loadFilesByUser(String user) {

        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#usr", "user");  // Crear alias para "user"


        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userVal", new AttributeValue().withS(user)); // Valor del usuario


        DynamoDBQueryExpression<FileDb> queryExpression = new DynamoDBQueryExpression<FileDb>()
                .withIndexName("user-index")
                .withKeyConditionExpression("#usr = :userVal")
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);

        return dynamoDBMapper.query(FileDb.class, queryExpression);
    }

    public void deleteFile(String id, String user) {
        FileDb fileRecord = new FileDb();
        fileRecord.setId(id);
        fileRecord.setUser(user);
        dynamoDBMapper.delete(fileRecord);
    }

    public String getFileKey(String id, String user) {
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#usr", "user");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":idVal", new AttributeValue().withS(id));
        expressionAttributeValues.put(":userVal", new AttributeValue().withS(user));

        DynamoDBQueryExpression<FileDb> queryExpression = new DynamoDBQueryExpression<FileDb>()
                .withKeyConditionExpression("id = :idVal and #usr = :userVal")
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues);

        List<FileDb> fileRecords = dynamoDBMapper.query(FileDb.class, queryExpression);

        if (fileRecords.size() > 0) {
            return fileRecords.get(0).getFileName();
        } else {
            throw new RuntimeException("File not found for id: " + id + " and user: " + user);
        }
    }

    public List<FileDb>  findFilebyNameAndUser(String fileName, String user) {
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#usr", "user");
        expressionAttributeNames.put("#fileName", "fileName");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":userVal", new AttributeValue().withS(user));
        expressionAttributeValues.put(":fileNameVal", new AttributeValue().withS(fileName));

        DynamoDBQueryExpression<FileDb> queryExpression = new DynamoDBQueryExpression<FileDb>()
                .withIndexName("user-fileName-index")
                .withKeyConditionExpression("#usr = :userVal and #fileName = :fileNameVal")
                .withExpressionAttributeNames(expressionAttributeNames)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false);

        return dynamoDBMapper.query(FileDb.class, queryExpression);
    }

    public S3Object getS3Object(String id, String user, String bucket) {
        String s3key = getFileKey(id,user);

        return  amazonS3.getObject(new GetObjectRequest(bucket, s3key));
    }

    public void updateFileRecord(String fileName,  String s3Url, String user, List<FileDb> fileRecords) {
        if (!fileRecords.isEmpty()) {
            FileDb fileRecord = fileRecords.get(0);
            fileRecord.setS3Url(s3Url);
            fileRecord.setUploadTimestamp(Instant.now().toString());
            dynamoDBMapper.save(fileRecord);
        }
    }
}
