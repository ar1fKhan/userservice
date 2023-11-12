package dev.arif.userservice.security;

import dev.arif.userservice.models.User;
import dev.arif.userservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // This is a service class

public class CustomSpringUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomSpringUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser  = userRepository.findByEmail(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User doesn't exist");
        }
        User user = optionalUser.get();
        return new CustomSpringUserDetails(user);


    }
}
