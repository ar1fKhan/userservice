package dev.arif.userservice.service;

import dev.arif.userservice.dtos.UserDto;
import dev.arif.userservice.models.Role;
import dev.arif.userservice.models.Session;
import dev.arif.userservice.models.SessionStatus;
import dev.arif.userservice.models.User;
import dev.arif.userservice.repository.SessionRepository;
import dev.arif.userservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            //            return this.signUp(email, password);
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong username password");
            //            return null;
        }

       // String token = RandomStringUtils.randomAlphanumeric(30);

        MacAlgorithm alg = Jwts.SIG.HS256; //or HS384 or HS256
        SecretKey key = alg.key().build();

        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("email", user.getEmail());
//        jsonForJwt.put("roles", user.getRoles());
        jsonForJwt.put("createdAt", new Date());
        jsonForJwt.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        String token = Jwts.builder()
                .claims(jsonForJwt)
                .signWith(key, alg)
                .compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);

        sessionRepository.save(session);

        UserDto userDto = UserDto.from(user);

        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:" + token);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, org.springframework.http.HttpStatus.OK);



        return response;

    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    public UserDto signUp(String email, String password) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            return null;
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }
    public SessionStatus validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return SessionStatus.ENDED;
        }

        Session session = sessionOptional.get();

        if (session.getSessionStatus() == SessionStatus.ENDED) {
            return SessionStatus.ENDED;
        }

        Jws<Claims> claimsJws = Jwts.parser()
                .build()
                .parseSignedClaims(token);

        String email = (String) claimsJws.getPayload().get("email");
        List<Role> roles = (List<Role>) claimsJws.getPayload().get("roles");
        Date createdAt = (Date) claimsJws.getPayload().get("createdAt");

        if(createdAt.before(new Date())){
            return SessionStatus.ENDED;
        }

        return SessionStatus.ACTIVE;
    }





}
