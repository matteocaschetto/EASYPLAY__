package com.epicode.EASYPLAY.security;

import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;



@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UtenteRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Utente user = repo.findByUsername(username).orElseThrow();


        UserDetails dettagliUtente = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRuolo().toUpperCase()) // Assicurati che sia ROLE_USER o ROLE_ADMIN
                .build();


        return dettagliUtente;
    }
}
