package com.socialnetwork.util;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	String upload(String bucketName, MultipartFile file) throws IOException;
	public void delete(String bucketName, String blobName);
}
