package com.epicode.EASYPLAY.repository;

import com.epicode.EASYPLAY.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    public Optional<Utente> findByUsername(String username);

    public boolean existsByUsernameAndPassword(String username, String password);

    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);

    Optional<Utente> findById (Long id);
}