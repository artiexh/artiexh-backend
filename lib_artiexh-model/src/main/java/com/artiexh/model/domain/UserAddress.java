package com.artiexh.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class UserAddress {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	@NotEmpty
	private String address;
	@NotNull
	private Type type;
	private Boolean isDefault = false;

	@Getter
	public enum Type {
		HOME(1),
		OFFICE(2);

		private final int value;

		Type(int value) {
			this.value = value;
		}

		public static Type from(Byte value) {
			return switch (value) {
				case 1 -> HOME;
				case 2 -> OFFICE;
				default -> throw new IllegalArgumentException("Unknown value: " + value);
			};
		}
	}
}
