package com.bookmyshow.services;

import com.bookmyshow.models.User;
import com.bookmyshow.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final ConcurrentHashMap<Long, User> userMap = new ConcurrentHashMap<>();

    public User createUser(User user) {
        System.out.println("Received User Data: " + user.getName() + ", " + user.getEmail() + ", " + user.getPhoneNumber());
        user.setPassword(getHashedPassword(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByPhoneNumberAndPassword(Long phoneNumber, String password) {
    	User user = userRepository.findByPhoneNumber(phoneNumber)
    			.orElseThrow(() -> new RuntimeException("User not found"));
    	
    	String hashedPassword = getHashedPassword(password);
    	
    	if (user.getPassword().equals(hashedPassword)) {
    		return user;
    	} else {
    		throw new RuntimeException("Invalid credentials");
    	}
    }
    
    public Long getUserIdByPhoneNumber(Long phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userMap.values().stream().toList();
    }

    public String getHashedPassword(String password1) {
        try {
            return toHexString(getSHA(password1));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    public byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }
}
