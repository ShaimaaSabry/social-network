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
@Table(name="posts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Post {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "post_seq_generator")
	@SequenceGenerator(name="post_seq_generator", sequenceName="posts_id_seq", allocationSize = 1)
	private Long Id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private String content;
}
