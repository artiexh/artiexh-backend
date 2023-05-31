package com.artiexh.model.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
	private Integer page;
	private Integer pageSize;
	private Long totalSize;
	private Integer totalPage;
	private List<T> items;

	public PageResponse(Page page) {
		if (page == null) {
			return;
		}
		this.page = page.getNumber() + 1;
		this.pageSize = page.getSize();
		this.totalSize = page.getTotalElements();
		this.totalPage = page.getTotalPages();
	}

}

