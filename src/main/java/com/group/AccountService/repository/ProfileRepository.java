package com.group.AccountService.repository;

import com.group.AccountService.model.Profile;
import com.group.AccountService.model.User;
import com.group.AccountService.repository.ProfileRepository;
import com.group.AccountService.model.Preference;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, ObjectId> {
}
