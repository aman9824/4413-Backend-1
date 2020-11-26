package com.project.bookstore.service;

import com.project.bookstore.common.Util;
import com.project.bookstore.common.WConstants;
import com.project.bookstore.model.UserEntity;
import com.project.bookstore.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  PasswordEncoder encoder;

  public String singupUser(UserEntity userEntity){
    JSONObject json = new JSONObject();
    json.put("status", WConstants.RESPONSE_SUCCESS);

    if(!userEntity.isValid()){
      return Util.getJsonResponse(WConstants.INVALID_USER_SIGNUP_DATA, null);
    }

    if(userRepository.isUserExist(userEntity.getEmail())){
      json.put("message", "User already exists. Please try logging in.");
      return json.toString();
    }

    userEntity.setPassword(encoder.encode(userEntity.getPassword()));
    if(userRepository.signupUser(userEntity) == 1){
      json.put("message", "Sign up successful for user: " + userEntity.getEmail());
      return json.toString();
    }

    return Util.getJsonResponse(WConstants.RESULT_UNKNOWN_ERROR, null);
  }

  public String loginUser(String email, String password){
    JSONObject json = new JSONObject();

    UserEntity user = userRepository.findUser(email);
    if(user == null){ // user not found
      return Util.getJsonResponse(WConstants.RESULT_USER_DOES_NOT_EXIST, email);
    }
    if(!encoder.matches(password, user.getPassword())){ // if user found but incorrect password is entered
      return Util.getJsonResponse(WConstants.RESULT_INVALID_CREDENTIALS, user.getUser_id());
    }
    json.put("userType", user.getUser_type());
    json.put("firstName", user.getFirst_name());
    json.put("lastName", user.getLast_name());
    json.put("userId", user.getUser_id());
    json.put("status", WConstants.RESPONSE_SUCCESS);
    return json.toString();
  }

}