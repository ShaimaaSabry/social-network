package com.socialnetwork.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepositoryBasic extends CrudRepository<PhotoEntity, Long> {

}
