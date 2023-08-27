package com.artiexh.api.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class S3Config {
	@Value("${artiexh.aws.s3.region}")
	private String region;
	@Value("${artiexh.aws.s3.public-bucket-name}")
	private String publicBucketName;
	@Value("${artiexh.aws.s3.private-bucket-name}")
	private String privateBucketName;
	@Value("${artiexh.aws.s3.access-key}")
	private String accessKey;
	@Value("${artiexh.aws.s3.secret-key}")
	private String secretKey;

	@Bean
	public AmazonS3 initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		return AmazonS3ClientBuilder
			.standard()
			.withRegion(region)
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.build();
	}
}
