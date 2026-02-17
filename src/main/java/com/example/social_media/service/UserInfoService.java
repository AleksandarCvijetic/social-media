package com.example.social_media.service;


import com.example.dto.UserInfoDto;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.mapper.UserInfoMapper;
import com.example.social_media.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserInfoService implements UserDetailsService {

    private final UserInfoRepository repository;
    private final PasswordEncoder encoder;
    private final UserInfoMapper userMapper;

    @Autowired
    public UserInfoService(UserInfoRepository repository, PasswordEncoder encoder, UserInfoMapper userMapper) {
        this.repository = repository;
        this.encoder = encoder;
        this.userMapper = userMapper;
    }

    // Method to load user details by username (email)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database by email (username)
        Optional<UserInfo> userInfo = repository.findByEmail(username);
        
        if (userInfo.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        
        // Convert UserInfo to UserDetails (UserInfoDetails)
        UserInfo user = userInfo.get();
            return new UserInfoDetails(user);
    }

    public List<UserInfoDto> searchUsers(String keyword, UserInfo currentUser) {
        List<UserInfo> users = repository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(keyword, keyword);

        // Filtriramo da ne uključimo trenutno prijavljenog korisnika i njegove prijatelje
        users.removeIf(u -> u.getId().equals(currentUser.getId()) || currentUser.getFriends().contains(u));

        return users
                    .stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
    }

    public UserInfo findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserInfo findById(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Add any additional methods for registering or managing users
    public String addUser(UserInfo userInfo) {
        // PROVERA EMAIL-A
        if (repository.existsByEmail(userInfo.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // Encrypt password before saving
        userInfo.setPassword(encoder.encode(userInfo.getPassword())); 
        userInfo.setRoles("ROLE_USER");
        repository.save(userInfo);
        return "User added successfully!";
    }

    
    public void addFriend(UserInfo user, UserInfo friend){
        if (user.getFriends().contains(friend) || friend.getFriends().contains(user)) {
            throw new IllegalStateException("Users are already friends");
        }

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        repository.save(user);
        repository.save(friend);
    }
    
    public void removeFriend(UserInfo user, UserInfo friend){
        if (!user.getFriends().contains(friend) || !friend.getFriends().contains(user)) {
            throw new IllegalStateException("Users are not friends");
        }
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        repository.save(user);
        repository.save(friend);
    }

}
