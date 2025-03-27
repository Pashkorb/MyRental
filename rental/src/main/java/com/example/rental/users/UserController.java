package com.example.rental.users;

import com.example.rental.security.JwtTokenUtil;
import com.example.rental.users.dto.UserDataRequest;
import com.example.rental.users.dto.UserFulRequest;
import com.example.rental.users.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/rental/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UserDataRequest user)
    {
            UUID uuid=jwtTokenUtil.getUserIdFromToken(authorization);
        if (!uuid.equals(user.getUuid())){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);}
        userService.createUser(new User(uuid,user.getEmail()));
    return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
        UserResponse user = userService.getUser(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserFulRequest user) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponse updatedUser = userService.updateUser(user,userPrincipal.getUserId());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Void> deleteUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userService.getUser(userPrincipal.getUserId()) != null) {
            userService.deleteUser(userPrincipal.getUserId());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}