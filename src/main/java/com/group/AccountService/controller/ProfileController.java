package com.group.AccountService.controller;

import java.util.Collections;
import java.util.Map;

import com.group.AccountService.service.UserService;
import com.group.AccountService.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group.AccountService.model.Profile;
import com.group.AccountService.service.ProfileService;

import com.group.AccountService.documentation.ProfileApi;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController implements ProfileApi {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    @Override
    @GetMapping()
    public ResponseEntity<List<Profile>> getAllProfile() {
        List<Profile> profiles = profileService.allProfiles();
        return ResponseEntity.ok(profiles);
    }

    @Override
    @GetMapping("user")
    public ResponseEntity<Profile> getProfileByUser(@RequestParam String userId) {
        return ResponseEntity.ok(profileService.getProfileByUser(userId));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Profile> getSingleProfile(@PathVariable String id) {
        Profile profile = profileService.singleProfile(id);
        return ResponseEntity.ok(profile);
    }

    @Override
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable String userId, @RequestBody Map<String, Object> payload) {
        try {
            User user = profileService.update(userId, payload);
            return ResponseEntity.ok(user);

        } catch (Exception error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", error.getMessage()));
        }
    }
}
