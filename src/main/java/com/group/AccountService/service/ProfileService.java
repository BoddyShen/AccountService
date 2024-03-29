package com.group.AccountService.service;

import com.group.AccountService.model.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.AccountService.model.Preference;
import com.group.AccountService.model.User;
import com.group.AccountService.repository.PreferenceRepository;
import com.group.AccountService.repository.ProfileRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.lang.reflect.Field;

@Service
public class ProfileService {
    public ProfileService() {

    }

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private UserService userService;


    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Profile> allProfiles() {
        return profileRepository.findAll();
    }

    public Profile singleProfile(String id) {
        ObjectId profileId = TypeUtil.objectIdConverter(id);
        return profileRepository.findById(profileId).orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }


    public Profile create(Map<String, Object> profileMap) {
        // String[] keys = {"displayName", "gender", "age", "bio"};
        // Map<String, String> map = profileMap.entrySet().stream().filter(x -> x.getKey())
        Profile profile = new Profile(profileMap);
        profileRepository.insert(profile);

        userService.setProfile(profileMap.get("userId").toString(), profile);
        return profile;
    }

    public User update(String user_id, Map<String, Object> profileMap) {
        Profile profile = this.getProfileByUser(user_id);
        String[] skipFields = {"gender", "preferences"};
        Class<?> profileClass = Profile.class;
        Field[] fields = profileClass.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (profileMap.containsKey(fieldName) & !Arrays.asList(skipFields).contains(fieldName)) {
                TypeUtil.setField(profile, field, profileMap.get(fieldName));
            }
        }
        profile.setGender(TypeUtil.getGender(profileMap.get("gender").toString()));
        profile.setAge(DateUtil.getAge(profile.getBirthday()));
        updatePreferences(profile, TypeUtil.objectToListString(profileMap.get("preferences")));

        profileRepository.save(profile);
        User user = userService.singleUser(user_id);
        return user;
    }


    private void updatePreferences(Profile profile, List<String> preferenceNames) {
        List<Preference> preferences = new ArrayList<>();

        for (Integer ind = 0; ind < preferenceNames.size(); ind ++) {
            preferences.add(preferenceService.PreferenceByName(preferenceNames.get(ind)));
        }

        profile.setPreferences(preferences);
    }

    public Profile getProfileByUser(String userId) {
        User user = userService.singleUser(userId);
        return user.getProfile();
    }

    public void setProfilePhoto(String userId, String photoURL){
        Profile profile = getProfileByUser(userId);

        if (profile != null) {
            if (profile.getImageUrls() == null) {
                profile.setImageUrls(new ArrayList<>());
            }
            profile.getImageUrls().add(photoURL);
            profileRepository.save(profile);
        } else {
            throw new IllegalStateException("Profile not found for user id: " + userId);
        }
    }

}
