package com.epicode.EASYPLAY.service;



import com.epicode.EASYPLAY.exception.EmailDuplicateException;
import com.epicode.EASYPLAY.exception.UsernameDuplicateException;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.payload.request.RegistrazioneRequest;
import com.epicode.EASYPLAY.payload.response.LoginResponse;
import com.epicode.EASYPLAY.repository.UtenteRepository;
import com.epicode.EASYPLAY.security.JwtUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UtenteService {

    @Autowired
    UtenteRepository utenteRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @SneakyThrows
    public String newUtente(RegistrazioneRequest registrazione) {
        String passwordCodificata = passwordEncoder.encode(registrazione.getPassword());
        checkDuplicateKey(registrazione.getUsername(), registrazione.getEmail());
        Utente user = registrazioneRequest_Utente(registrazione);
        user.setPassword(passwordCodificata);
        // controllo assegnazione role
        if (registrazione.getRuolo() == null || registrazione.getRuolo().equals("USER")) {
            user.setRuolo("USER");
        } else if (registrazione.getRuolo().equals("ADMIN")) {
            user.setRuolo("ADMIN");
        } else {
            throw new RuntimeException("Errore: Il Valore inserito come ruolo non è valido!");
        }
        Long id = utenteRepo.save(user).getId();
        return "Nuovo utente " + user.getUsername() + "con id " + id + " è stato inserito correttamente";
    }

    public LoginResponse login(String username, String password){

        // 1. AUTENTICAZIONE DELL'UTENTE IN FASE DI LOGIN
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // 2. INSERIMENTO DELL'AUTENTICAZIONE UTENTE NEL CONTESTO DELLA SICUREZZA
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. RECUPERO RUOLI --> String
        String ruolo=null;
        for(Object role :authentication.getAuthorities()){
            ruolo=role.toString();
            break;
        }

        // 4. GENERO L'UTENTE
        Utente user = new Utente();
        user.setUsername(username);
        user.setRuolo(ruolo);

        // 5. GENERO IL TOKEN
        String token = jwtUtil.creaToken(user);

        // 6. CREO L'OGGETTO DI RISPOSTA AL CLIENT
        return new LoginResponse(username, token);
    }

    public String modificaAvatar(long idUtente, String urlImg) {
        Utente utente = utenteRepo.findById(idUtente).orElseThrow();
        utente.setAvatar(urlImg);
        return "Immagine dell'avatar modificata";
    }

    public void checkDuplicateKey(String username, String email) throws UsernameDuplicateException, EmailDuplicateException {

        if (utenteRepo.existsByUsername(username)) {
            throw new UsernameDuplicateException("Username già utilizzato, non disponibile");
        }

        if (utenteRepo.existsByEmail(email)) {
            throw new EmailDuplicateException("Email già utilizzata da un altro utente");
        }
    }

    public Utente registrazioneRequest_Utente(RegistrazioneRequest request) {
        Utente utente = new Utente();
        utente.setEmail(request.getEmail());
        utente.setNome(request.getNome());
        utente.setUsername(request.getUsername());
        utente.setCognome(request.getCognome());
        utente.setAvatar("https://www.fotoarreda.com/quadro-su-tela/stampa/personalizzata/immagine/790443592_1-1.html");
        return utente;
    }

    public Optional<Utente> findById(Long id){
        return utenteRepo.findById(id);
    }
}
