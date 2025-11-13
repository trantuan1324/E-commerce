package com.rabbyte.sbecom.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();

        String randomId = UUID.randomUUID().toString();
        String fileName = null;
        if (originalFileName != null)
            fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        // Check if path is exists, or else create it
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // upload image file
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }
}
