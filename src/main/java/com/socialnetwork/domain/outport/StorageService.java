package com.socialnetwork.domain.outport;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String upload(MultipartFile file) throws IOException;

    void delete(String path);
}
