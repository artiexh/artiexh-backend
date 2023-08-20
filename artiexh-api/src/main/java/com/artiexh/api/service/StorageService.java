package com.artiexh.api.service;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

public interface StorageService {
	void upload(MultipartFile file);
	S3Object download(String presignedUrl) throws MalformedURLException;
}
