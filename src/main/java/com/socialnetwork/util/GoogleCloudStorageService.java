package com.socialnetwork.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class GoogleCloudStorageService implements StorageService {
	
	@Override
	public String upload(String bucketName, MultipartFile file) throws IOException {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		Bucket bucket = storage.get(bucketName);
		
		InputStream inputStream =  new BufferedInputStream(file.getInputStream());
	    Blob blob = bucket.create(file.getOriginalFilename(), inputStream, file.getContentType());
	    return blob.getName();
	}
	
	@Override
	public void delete(String bucketName, String blobName) {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		
		BlobId blobId = BlobId.of(bucketName, blobName);
		storage.delete(blobId);
	}
}
