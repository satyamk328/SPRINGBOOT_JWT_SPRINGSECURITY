package com.auth.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_master", uniqueConstraints = { @UniqueConstraint(columnNames = { "UserName" }),
		@UniqueConstraint(columnNames = { "Email" }) })
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="UserId", nullable=false, unique=true)
	private Long userId;
	@Column(name="Name")
	private String name;
	@Column(name="UserName")
	private String username;
	@Column(name="Email")
	private String email;
	@Column(name="Password")
	private String password;
	@Column(name="Address")
	private String address;
	@Column(name="PanNumber")
	private String panNumber;
	@Column(name="PhoneNumber")
	private String phoneNumber;
	@Column(name="City")
	private String city;
	@Column(name="State")
	private String state;
	@Column(name="Country")
	private String country;
	@Column(name="IsLock")
	private boolean isLock;
	@Column(name="IsActive")
	private boolean isActive;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "UserId"), inverseJoinColumns = @JoinColumn(name = "RoleId"))
	private Set<Role> roles;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		final List<GrantedAuthority> authorities = new ArrayList<>();
		try {
			for (final Role role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getName().name()));
			}
		} catch (final Exception e) {
			authorities.add(new SimpleGrantedAuthority("USER"));
		}
		if (authorities.size() == 0) {
			authorities.add(new SimpleGrantedAuthority("USER"));
		}
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.isLock;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
}