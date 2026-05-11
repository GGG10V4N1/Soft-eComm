package com.soft.ecommerce.controller;

import com.soft.ecommerce.config.AppConstants;
import com.soft.ecommerce.payload.AuthenticationResult;
import com.soft.ecommerce.payload.PageResponse;
import com.soft.ecommerce.payload.UserDTO;
import com.soft.ecommerce.security.request.LoginRequest;
import com.soft.ecommerce.security.request.SignUpRequest;
import com.soft.ecommerce.security.response.MessageResponse;
import com.soft.ecommerce.service.api.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ecomApi/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthenticationResult result = authService.login(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        result.getJwtCookie().toString())
                .body(result.getResponse());
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser(){
        ResponseCookie cookie = authService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authService.register(signUpRequest);
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null) return authentication.getName();
        return "";
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        return ResponseEntity.ok().body(authService.getCurrentUserDetails(authentication));
    }


    @GetMapping("/sellers")
    public ResponseEntity<?> getAllSellers(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER,
                                                         required = false) Integer pageNumber) {
        PageResponse<UserDTO> = authService.getAllSellers();
        return ResponseEntity.ok();
    }


}
