package com.artiexh.model.rest.file;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileRequest {
	private MultipartFile file;
}
