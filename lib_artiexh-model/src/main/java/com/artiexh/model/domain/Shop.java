package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    private Long id;
    private String shopName;
	private String shopImageUrl;
    //private Artist owner;
}
