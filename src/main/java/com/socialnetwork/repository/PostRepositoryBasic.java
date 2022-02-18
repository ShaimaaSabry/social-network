package com.socialnetwork.repository;

import com.socialnetwork.domain.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepositoryBasic extends CrudRepository<PostEntity, Long> {
    Collection<PostEntity> findAllByUserId(long userId);
}
