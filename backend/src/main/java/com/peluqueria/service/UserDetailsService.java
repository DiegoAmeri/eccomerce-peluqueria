package com.peluqueria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peluqueria.model.User;
import com.peluqueria.repository.UserRepository;
import com.peluqueria.security.UserPrincipal;

@Service
public class UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return UserPrincipal.create(user);
    }
}
