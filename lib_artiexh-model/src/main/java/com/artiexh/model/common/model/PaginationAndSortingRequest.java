package com.artiexh.model.common.model;

import com.artiexh.model.common.constant.PaginationAndSorting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationAndSortingRequest {
	private Integer pageSize = PaginationAndSorting.DEFAULT_PAGE_SIZE;
	private Integer pageNumber = PaginationAndSorting.DEFAULT_PAGE_NUMBER;
	private String sortBy = PaginationAndSorting.DEFAULT_SORT_BY;
	private Sort.Direction sortDirection = PaginationAndSorting.DEFAULT_SORT_DIRECTION;

	public Pageable getPageable() {
		return PageRequest.of(pageNumber - 1, pageSize, sortDirection, sortBy);
	}
}

