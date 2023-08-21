package com.artiexh.data.opensearch.model;

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
	@Field(name = "owner", type = FieldType.Object)
	private Owner owner;
	@Field(name = "status", type = FieldType.Byte)
	private Byte status;
	@Field(name = "name", type = FieldType.Text)
	private String name;
	@Field(name = "price", type = FieldType.Object)
	private Money price;
	@Field(name = "category", type = FieldType.Object)
	private Category category;
	@Field(name = "type", type = FieldType.Byte)
	private Byte type;
	@Field(name = "publishDatetime", type = FieldType.Date)
	private Instant publishDatetime;
	@Field(name = "averageRate", type = FieldType.Float)
	private Float averageRate;
	@Field(name = "tags", type = FieldType.Keyword)
	private String[] tags;

	@Data
	public static class Money {
		@Field(name = "amount", type = FieldType.Float)
		private BigDecimal amount;
		@Field(name = "unit", type = FieldType.Keyword)
		private String unit;
	}

	@Data
	public static class Owner {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "username", type = FieldType.Text)
		private String username;
		@Field(name = "displayName", type = FieldType.Text)
		private String displayName;
		@Field(name = "province", type = FieldType.Object)
		private Province province;
	}

	@Data
	public static class Province {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
		@Field(name = "country", type = FieldType.Object)
		private Country country;
	}

	@Data
	public static class Country {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
	}

	@Data
	public static class Category {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
	}
}
