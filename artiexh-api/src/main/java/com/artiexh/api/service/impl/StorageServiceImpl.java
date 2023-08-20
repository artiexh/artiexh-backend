package com.artiexh.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PresignedUrlDownloadRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.artiexh.api.config.S3Config;
import com.artiexh.api.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
	private final S3Config s3Config;
	@Override
	public void upload(MultipartFile multipartFile) {
		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			fileUrl = "https://" + s3Config.getRegion() + "/" + s3Config.getBucketName() + "/" + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public S3Object download(String preSignedUrl) throws MalformedURLException {
		URL url = new URL(preSignedUrl);
		return s3Config.initializeAmazon().download(new PresignedUrlDownloadRequest(
			url
		)).getS3Object();
	}

	private String generateFileName(MultipartFile multiPart) {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		AmazonS3 s3 = s3Config.initializeAmazon();
		s3.putObject(new PutObjectRequest(s3Config.getBucketName(), fileName, file)
			.withCannedAcl(CannedAccessControlList.PublicRead));
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
}
