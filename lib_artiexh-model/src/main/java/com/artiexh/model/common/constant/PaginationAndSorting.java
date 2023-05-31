package com.artiexh.model.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationAndSorting {
	public static final Integer DEFAULT_PAGE_SIZE = 5;
	public static final Integer DEFAULT_PAGE_NUMBER = 1;
	//TODO: Add audit change to createAt
	public static final String DEFAULT_SORT_BY = "id";
	public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

}
