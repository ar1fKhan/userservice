package dev.arif.userservice.controller;

import dev.arif.userservice.dtos.LoginRequestDto;
import dev.arif.userservice.dtos.LogoutRequestDto;
import dev.arif.userservice.dtos.SignUpRequestDto;
import dev.arif.userservice.dtos.UserDto;
import dev.arif.userservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) {
        return authService.login(request.getEmail(), request.getPassword());


    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        return authService.logout(request.getToken(), request.getUserId());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto request) {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        if(userDto == null){
            return new ResponseEntity<>(userDto, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

     /*  @GetMapping("/signup")
    public String signUp() {
        UserDto userDto = authService.signUp(request.getEmail(), request.getPassword());
        return "hello";
   }*/

}
