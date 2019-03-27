package com.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RoleId", unique = true, nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(length = 60, name = "RoleName")
	private RoleName name;

	@Column(name = "description")
	private String description;

}
