package com.artiexh.data.opensearch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Data
@Document(indexName = "product")
public class ProductDocument {
	@Id
	private Long id;
	@Field(name = "owner", type = FieldType.Object)
	private Owner owner;
	@Field(name = "shop", type = FieldType.Object)
	private Shop shop;
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
	@Field(name = "averageRate", type = FieldType.Float)
	private Float averageRate;
	@Field(name = "tags", type = FieldType.Keyword)
	private String[] tags;
	@Field(name = "campaignId", type = FieldType.Long)
	private Long campaignId;
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
	}

	@Data
	public static class Shop {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "shopName", type = FieldType.Text)
		private String shopName;
		@Field(name = "shopWard", type = FieldType.Object)
		private Ward shopWard;
		@Field(name = "shopAddress", type = FieldType.Keyword)
		private String shopAddress;
	}

	@Data
	public static class Ward {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
		@Field(name = "fullName", type = FieldType.Keyword)
		private String fullName;
		@Field(name = "district", type = FieldType.Object)
		private District district;
	}

	@Data
	public static class District {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
		@Field(name = "fullName", type = FieldType.Keyword)
		private String fullName;
		@Field(name = "province", type = FieldType.Object)
		private Province province;
	}

	@Data
	public static class Province {
		@Field(name = "id", type = FieldType.Long)
		private Long id;
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
		@Field(name = "fullName", type = FieldType.Keyword)
		private String fullName;
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
