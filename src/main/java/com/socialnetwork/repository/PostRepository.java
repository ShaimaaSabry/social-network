package com.socialnetwork.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.socialnetwork.domain.Post;

@Repository
public interface PostRepository extends CrudRepository<Post, Long>{
	List<Post> findAllByUserId(long userId);
}
