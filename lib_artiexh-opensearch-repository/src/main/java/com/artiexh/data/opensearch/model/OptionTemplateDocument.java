package com.artiexh.data.opensearch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

@Data
@Document(indexName = "option_template")
public class OptionTemplateDocument {
	@Id
	private Long id;
	@Field(name = "name", type = FieldType.Keyword)
	private String name;
	@Field(name = "optionValues", type = FieldType.Object)
	private Set<OptionValue> optionValues;

	@Data
	public static class OptionValue {
		@Field(name = "name", type = FieldType.Keyword)
		private String name;
		@Field(name = "value", type = FieldType.Keyword)
		private String value;
	}
}
