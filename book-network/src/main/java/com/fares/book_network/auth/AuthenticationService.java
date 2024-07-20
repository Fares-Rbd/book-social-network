package com.fares.book_network.auth;


import com.fares.book_network.email.EmailService;
import com.fares.book_network.email.EmailTemplateName;
import com.fares.book_network.role.RoleRepository;
import com.fares.book_network.security.JwtService;
import com.fares.book_network.user.Token;
import com.fares.book_network.user.TokenRepository;
import com.fares.book_network.user.User;
import com.fares.book_network.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    private final AuthenticationManager authenticationManager;
    
    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                // todo - handle exception in a better way
                .orElseThrow(()->new IllegalStateException(" 'USER' role not found"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false) //the user will enable the account via email validation
                .accountLocked(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);    
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(user.getEmail(),
                user.getfullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation Email");
        
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken= generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15)) //the activation code will expire in 15 minutes
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom(); //this is a cryptographically strong random number generator (RNG)
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(chars.length());
            stringBuilder.append(chars.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())

        ); //takes care of the authentication process

        var claims = new HashMap<String, Object>();
        var user= ((User)auth.getPrincipal());

        claims.put("fullName", user.getfullName());

        var jwtToken= jwtService.generateToken(claims, user);


        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }


//    @Transactional
    public void     activateAccount(String token) throws MessagingException {
        Token savedToken= tokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Token not found"));
    if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
        sendValidationEmail(savedToken.getUser());
        throw new RuntimeException("Activation Token Has Expired. A New Token Has Been Sent");
    }
    var user = savedToken.getUser();

    user.setEnabled(true);
    userRepository.save(user);
    savedToken.setValidatedAt(LocalDateTime.now()); //update the validation state of the token
    tokenRepository.save(savedToken);
    }
}
