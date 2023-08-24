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
import com.artiexh.api.utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	public S3Object download(String fileName) throws MalformedURLException {
		URL url = new URL(getPresignedString(s3Config.getRegion(), s3Config.getBucketName(), s3Config.getAccessKey(), s3Config.getSecretKey(), fileName));
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

	private String getStringToSign() {
		String stringToSign = "";

		return stringToSign;
	}

	private String getPresignedString(String regionName, String bucketName, String awsAccessKey, String awsSecretKey, String fileName) {
		URL endpointUrl;
		try {
			if (regionName.equals("us-east-1")) {
				endpointUrl = new URL("https://s3.amazonaws.com/" + bucketName + "/" + fileName);
			} else {
				endpointUrl = new URL("https://s3-" + regionName + ".amazonaws.com/" + bucketName + "/" + fileName);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Unable to parse service endpoint: " + e.getMessage());
		}

		// construct the query parameter string to accompany the url
		Map<String, String> queryParams = new HashMap<String, String>();

		// for SignatureV4, the max expiry for a presigned url is 7 days,
		// expressed in seconds
		int expiresIn = 7 * 24 * 60 * 60;
		queryParams.put("X-Amz-Expires", "" + expiresIn);

		// we have no headers for this sample, but the signer will add 'host'
		Map<String, String> headers = new HashMap<String, String>();

		AWS4SignerForQueryParameterAuth signer = new AWS4SignerForQueryParameterAuth(
			endpointUrl, "GET", "s3", regionName);
		String authorizationQueryParameters = signer.computeSignature(headers,
			queryParams,
			AWS4SignerBase.UNSIGNED_PAYLOAD,
			awsAccessKey,
			awsSecretKey);

		// build the presigned url to incorporate the authorization elements as query parameters
		return endpointUrl.toString() + "?" + authorizationQueryParameters;
	}
}
