package com.sports.store.controller;


import com.sports.store.payload.request.SignupRequest;
import com.sports.store.payload.response.MessageResponse;
import com.sports.store.repository.RoleRepository;
import com.sports.store.repository.UserRepository;
import com.sports.store.models.ERole;
import com.sports.store.models.Role;
import com.sports.store.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        try {
            List<User> users = new ArrayList<User>();
            Pageable paging = PageRequest.of(page, size);

            Page<User> pageUsers;
            if (username == null)
                pageUsers = userRepository.findAll(paging);
            else
                pageUsers = userRepository.findByUsernameContaining(username, paging);

            users = pageUsers.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("currentPage", pageUsers.getNumber());
            response.put("totalItems", pageUsers.getTotalElements());
            response.put("totalPages", pageUsers.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("addRoleToUser")
    public ResponseEntity<?> addRoleToUser(
            @RequestParam(required = true) long id,
            @RequestParam(required = true) String role) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User no exists"));
        }

      User user = userRepository.getOne(id);
        user.setUpdatedAt(new SimpleDateFormat("dd-MM-yyyy hh:mm").format(Calendar.getInstance().getTime()));
        Role userRole = new Role();

       if(role == "user" || role.equals("user")){
            userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        }else if(role == "admin" || role.equals("admin")){
            userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }
        user.getRoles().add(userRole);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Role "+userRole.getName()+" has added successfully!"));
    }

    @PutMapping("deleteRoleFromUser")
    public ResponseEntity<?> deleteRoleToUser(
            @RequestParam(required = true) long id,
            @RequestParam(required = true) String role) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User no exists"));
        }

        User user = userRepository.getOne(id);
        user.setUpdatedAt(new SimpleDateFormat("dd-MM-yyyy hh:mm").format(Calendar.getInstance().getTime()));
        Role userRole = new Role();

        if(role == "user" || role.equals("user")){
            userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        }else if(role == "admin" || role.equals("admin")){
            userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }
        user.getRoles().remove(userRole);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Role "+userRole.getName()+" has deleted successfully!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User is not exists!"));
        }
        try {
            userRepository.deleteById(userId);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateProfile/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @Valid @RequestBody SignupRequest user) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User is not exists!"));
        }

        User userToUpdate = userRepository.getOne(id);
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPassword(encoder.encode(user.getPassword()));
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy hh:mm").format(Calendar.getInstance().getTime());
        userToUpdate.setUpdatedAt(timeStamp);
		userRepository.save(userToUpdate);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
