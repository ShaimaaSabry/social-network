package com.socialnetwork.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "user_seq_generator")
	@SequenceGenerator(name="user_seq_generator", sequenceName="users_id_seq", allocationSize = 1)
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	@Column(unique=true)
	private String email;
	
	private boolean emailVerified;
	
	private String passwordHash;
	
	@OneToOne
    @JoinColumn(name = "profilepicture_id")
	private ProfilePicture profilePicture;
}
