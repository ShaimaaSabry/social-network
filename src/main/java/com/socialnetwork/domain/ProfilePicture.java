package com.socialnetwork.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="profilepictures")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfilePicture {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "profilepicture_seq_generator")
	@SequenceGenerator(name="profilepicture_seq_generator", sequenceName="profilepictures_id_seq", allocationSize = 1)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private String bucketName;
	
	private String blobName;
	
	private boolean validSelfie;
}
