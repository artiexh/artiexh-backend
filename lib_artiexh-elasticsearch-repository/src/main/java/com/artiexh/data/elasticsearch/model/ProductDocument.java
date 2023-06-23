package com.artiexh.data.elasticsearch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Document(indexName = "product")
public class ProductDocument {
	@Id
	private Long id;
	@Field(name = "name", type = FieldType.Text)
	private String name;
	@Field(name = "status", type = FieldType.Byte)
	private Byte status;
	@Field(name = "owner", type = FieldType.Object)
	private Owner owner;
	@Field(name = "category", type = FieldType.Object)
	private Category category;
	@Field(name = "price", type = FieldType.Float)
	private BigDecimal price;
	@Field(name = "averageRate", type = FieldType.Float)
	private Float averageRate;
	@Field(name = "type", type = FieldType.Byte)
	private Byte type;
	@Field(name = "publishDatetime", type = FieldType.Date)
	private Instant publishDatetime;

	public static class Owner {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "username", type = FieldType.Text)
		private String username;
		@Field(name = "name", type = FieldType.Text)
		private String displayName;
		@Field(name = "province", type = FieldType.Object)
		private Province province;
	}

	public static class Province {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
		@Field(name = "province", type = FieldType.Object)
		private Country country;
	}

	public static class Country {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
	}

	public static class Category {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
	}
}
