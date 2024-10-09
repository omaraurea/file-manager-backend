package com.wd.filemanager.repository;

import com.wd.filemanager.model.FileDb;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableScan
public interface FileDbRepository  extends CrudRepository <FileDb, String>{
    Optional<FileDb> findById(String id);

}
