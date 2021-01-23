package com.socialnetwork.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.socialnetwork.domain.ProfilePicture;

@Repository
public interface ProfilePictureRepository extends CrudRepository<ProfilePicture, Long>{

}
