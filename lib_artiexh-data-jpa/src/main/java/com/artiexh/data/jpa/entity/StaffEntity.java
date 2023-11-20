package com.artiexh.data.jpa.entity;

import com.sun.jna.platform.win32.Advapi32Util;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staff")
public class StaffEntity extends AccountEntity {
}
