package at.undok.auth.manager;

import at.undok.auth.exception.BadCredentialsException;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.serviceimpl.UserDetailsServiceImpl;
import at.undok.common.message.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Service
@RequiredArgsConstructor
public class UndokAuthenticationManager {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager springAuthenticationManager;

    public Authentication authenticate(LoginDto loginDto) throws AuthenticationException {
        checkPW(loginDto);
        return springAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
    }

    private void checkPW(LoginDto loginDto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());
        if (!passwordEncoder.matches(loginDto.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("wrong password");
        }
    }

}
