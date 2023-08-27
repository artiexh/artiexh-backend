package com.artiexh.api.service;

import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.model.rest.media.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

public interface StorageService {
	FileResponse upload(MultipartFile multipartFile, boolean isPublic) throws IOException;
	S3Object download(String fileName) throws MalformedURLException;
}
