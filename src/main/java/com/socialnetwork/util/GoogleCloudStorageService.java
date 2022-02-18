package com.socialnetwork.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.socialnetwork.config.GcpConfig;
import com.socialnetwork.domain.outport.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class GoogleCloudStorageService implements StorageService {
    @Autowired
    GcpConfig gcpConfig;

    @Override
    public String upload(MultipartFile file) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get(gcpConfig.getProfilePictureBucket());

        InputStream inputStream = new BufferedInputStream(file.getInputStream());
        Blob blob = bucket.create(file.getOriginalFilename(), inputStream, file.getContentType());

        return gcpConfig.getGsBasePath() + gcpConfig.getProfilePictureBucket() + "/" + blob.getName();
    }

    @Override
    public void delete(String path) {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        storage.delete("x");

//		BlobId blobId = BlobId.of(bucketName, blobName);
//		storage.delete(blobId);
    }
}
