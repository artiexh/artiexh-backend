package com.artiexh.model.rest.media;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse {
	private String presignedUrl;
	private String fileName;
}
