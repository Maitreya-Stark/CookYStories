package com.cookystoriesspring.CookYStories.User.Resolvers;


import com.cookystoriesspring.CookYStories.User.Models.GraphQLInputs.FollowerRelationship;
import com.cookystoriesspring.CookYStories.User.Models.User;
import com.cookystoriesspring.CookYStories.User.Models.UserProfile;
import com.cookystoriesspring.CookYStories.User.Models.GraphQLInputs.ProfileInput;
import com.cookystoriesspring.CookYStories.User.MongoRepositories.UserProfileRepository;
import com.cookystoriesspring.CookYStories.User.MongoRepositories.UserRepository;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.EventRecodingLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class UserGraphQLMutationController implements GraphQLMutationResolver {

    Logger log = LoggerFactory.getLogger(UserGraphQLMutationController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserProfileRepository userProfileRepository;


    @Transactional
    public Boolean signUpUser(User user) {

        User fetchedByUsername = userRepository.findByUsername(user.getUsername());
        User fetchedByEmail = userRepository.findByEmail(user.getEmail());

        if(fetchedByEmail!=null || fetchedByUsername!=null) {
            return false;
        }
        User createdUser = new User();
        createdUser.setFirstName(user.getFirstName());
        createdUser.setLastName(user.getLastName());
        createdUser.setEmail(user.getEmail());
        createdUser.setPassword(user.getPassword());
        createdUser.setUsername(user.getUsername());
        if(user.getBioDescription()!=null) { createdUser.setBioDescription(user.getBioDescription()); } else {createdUser.setBioDescription("");}
        if(user.getCity()!=null) { createdUser.setCity(user.getCity()); } else {createdUser.setCity("");}
        if(user.getCountry()!=null) {createdUser.setCountry(user.getCountry());} else {createdUser.setCountry("");}
        createdUser.setCreatedAt(new Date());
        User newUser = userRepository.insert(createdUser);

        User fetchedUser = userRepository.findByUsername(createdUser.getUsername());

        UserProfile userProfile = new UserProfile();
        userProfile.setBasicInfo(fetchedUser);
        userProfile.setFollowers(new ArrayList<>());
        userProfile.setFollowing(new ArrayList<>());
        userProfile.setNumFollowers(0);
        userProfile.setNumFollowing(0);
        userProfile.setNumPosts(0);
        userProfile.setPosts(new ArrayList<>());
        userProfile.setProfileImageUrl("");
        userProfile.setUsername(user.getUsername());

        UserProfile savedUserProfile = userProfileRepository.insert(userProfile);

        return true;
    }

    @Transactional
    public User updateUser(User user) {
        User fetchedUser = userRepository.findByUsername(user.getUsername());

        fetchedUser.setBioDescription(user.getBioDescription());
        fetchedUser.setFirstName(user.getFirstName());
        fetchedUser.setLastName(user.getLastName());
        fetchedUser.setCity(user.getCity());
        fetchedUser.setCountry(user.getCountry());
        if(userRepository.findByUsername(user.getUsername())!=null) {
            fetchedUser.setUsername(user.getUsername());
        }
        return userRepository.save(fetchedUser);
    }

    public Boolean removeUser(String username) {
        userProfileRepository.delete(userProfileRepository.findByUsername(username));
        userRepository.delete(userRepository.findByUsername(username));
        return true;
    }

    @Transactional
    public UserProfile updateProfileImage(ProfileInput profileInput) {
        UserProfile user = userProfileRepository.findByUsername(profileInput.getUsername());
        user.setProfileImageUrl(profileInput.getProfileImageUrl());
        return userProfileRepository.save(user);
    }

    public UserProfile followUser(FollowerRelationship followerRelationship) {
        log.info("to follow: " + followerRelationship.getIsFollow());

        UserProfile loggedInUser = userProfileRepository.findByUsername(followerRelationship.getLoggedInUser());
        UserProfile toUser = userProfileRepository.findByUsername(followerRelationship.getToFollowUser());

        if (!followerRelationship.getIsFollow()) {
            // unfollow

            List<User> toUserFollowers = toUser.getFollowers();
            List<User> loggedInUserFollowings = loggedInUser.getFollowing();

            if(toUserFollowers.contains(loggedInUser.getBasicInfo()) && loggedInUserFollowings.contains(toUser.getBasicInfo())) {
                toUserFollowers.remove(loggedInUser.getBasicInfo());
                toUser.setNumFollowers(toUser.getNumFollowers()-1);
                loggedInUserFollowings.remove(toUser.getBasicInfo());
                loggedInUser.setNumFollowing(loggedInUser.getNumFollowing()-1);

                toUser.setFollowers(toUserFollowers);
                loggedInUser.setFollowing(loggedInUserFollowings);

                userProfileRepository.save(toUser);
                userProfileRepository.save(loggedInUser);

                toUser.setIsFollowed(false);
            } else {
                if(toUserFollowers.contains(loggedInUser.getBasicInfo()) && loggedInUserFollowings.contains(toUser.getBasicInfo())) {
                    toUser.setIsFollowed(false);

                } else {
                    toUser.setIsFollowed(true);

                }
            }
        } else {
            // Follow


            List<User> toUserFollowers = new ArrayList<>();
            List<User> loggedInUserFollowings = new ArrayList<>();

            if (toUser.getFollowers() != null) {
                toUserFollowers = new ArrayList<>(List.copyOf(toUser.getFollowers()));
            }

            if (loggedInUser.getFollowing() != null) {
                loggedInUserFollowings = new ArrayList<>(List.copyOf(loggedInUser.getFollowing()));
            }

            if(!toUserFollowers.contains(loggedInUser.getBasicInfo()) && !loggedInUserFollowings.contains(toUser.getBasicInfo())) {
                log.info("Following User "+toUser.getUsername()+" by User "+loggedInUser.getUsername());

                toUserFollowers.add(loggedInUser.getBasicInfo());
                toUser.setNumFollowers(toUser.getNumFollowers()+1);
                loggedInUserFollowings.add(toUser.getBasicInfo());
                loggedInUser.setNumFollowing(loggedInUser.getNumFollowing()+1);

                toUser.setFollowers(toUserFollowers);
                loggedInUser.setFollowing(loggedInUserFollowings);

                userProfileRepository.save(toUser);
                userProfileRepository.save(loggedInUser);

                toUser.setIsFollowed(true);
            } else {

                if (!toUserFollowers.contains(loggedInUser.getBasicInfo()) && !loggedInUserFollowings.contains(toUser.getBasicInfo())) {
                    toUser.setIsFollowed(true);
                } else {
                    toUser.setIsFollowed(false);
                }
            }

        }

        return toUser;
    }
}
