package com.fares.book_network.file;


import com.fares.book_network.book.Book;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.currentTimeMillis;

@Service
@Slf4j //this annotation will add a logger to our class
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.upload.photos.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@NonNull MultipartFile sourceFile,
                           @NonNull Integer userId) {
        final String fileUploadSubPath = "users"+ File.separator + userId;
        return upLoadFile(sourceFile,fileUploadSubPath);
    }

    private String upLoadFile(@NonNull MultipartFile sourceFile,
                              @NonNull String fileUploadSubPath) {
        final String finalUploadPath= fileUploadPath + File.separator + fileUploadSubPath;
        File targetFoler = new File(finalUploadPath);
        if(!targetFoler.exists()){
            boolean folderCreated=targetFoler.mkdirs(); //mkdirs will make the folder and all of its subdirectories
            if(!folderCreated){
                log.error("Could not create the folder {}",finalUploadPath);
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File saved at {}",targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("Could not save the file {}",targetFilePath);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if(fileName ==null || fileName.isEmpty())
            return "";

        int lastDotIndex= fileName.lastIndexOf(".");
        if(lastDotIndex == -1)
            return "";
        return fileName.substring(lastDotIndex+1).toLowerCase();

    }
}
