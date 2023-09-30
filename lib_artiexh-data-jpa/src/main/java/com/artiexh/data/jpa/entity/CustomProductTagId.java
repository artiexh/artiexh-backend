package com.artiexh.data.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CustomProductTagId implements Serializable {

	private Long customProductId;

	private String name;

}