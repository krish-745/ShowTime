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

    @Autowired
    private ConcurrentHashMap<Long, User> userMap;

    public User createUser(User user) {
    	user.setPassword(getHashedPassword(user.getPassword()));
        User savedUser = userRepository.save(user);
        userMap.put(savedUser.getId(), savedUser);
        return savedUser;
    }

    public User getUserById(Long id) {
        return userMap.get(id);
    }
    
    public User getUserByIdAndPassword(Long id, String password) {
        User user = userMap.get(id);
        String password1 = getHashedPassword(password);
        if((user.getPassword()).equals(password1))
        	return user;
        else {
        	User user1 = new User();
        	return user1;
        }
    }

    public List<User> getAllUsers() {
        return userMap.values().stream().toList();
    }
    
    public String getHashedPassword(String password1) {
    	String password = "";
    	try {
        	password = toHexString(getSHA(password1));
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
    	return password;
    }
    
    public byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");
 
        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
    
    public String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
 
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
 
        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }
 
        return hexString.toString();
    }
}
