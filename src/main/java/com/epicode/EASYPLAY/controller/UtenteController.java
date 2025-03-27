package com.epicode.EASYPLAY.controller;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.epicode.EASYPLAY.exception.EmailDuplicateException;
import com.epicode.EASYPLAY.exception.UsernameDuplicateException;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.payload.UtenteInfoResponse;
import com.epicode.EASYPLAY.payload.EventoDTO;
import com.epicode.EASYPLAY.payload.request.LoginRequest;
import com.epicode.EASYPLAY.payload.request.RegistrazioneRequest;
import com.epicode.EASYPLAY.payload.response.LoginResponse;
import com.epicode.EASYPLAY.service.EventoService;
import com.epicode.EASYPLAY.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UtenteController {

    @Autowired
    UtenteService utenteService;

    @Autowired
    Cloudinary cloudinaryConfig;

    @Autowired
    private EventoService eventoService;


    @PostMapping("/new")
    public ResponseEntity<String> signUp(@Validated @RequestBody RegistrazioneRequest nuovoUtente, BindingResult validazione) throws UsernameDuplicateException, EmailDuplicateException {
        System.out.println("sono il metodo signup");
        if (validazione.hasErrors()) {
            StringBuilder errori = new StringBuilder("Problemi nella validazione dati :\n");

            for (ObjectError errore : validazione.getAllErrors()) {
                errori.append(errore.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(errori.toString(), HttpStatus.BAD_REQUEST);
        }

        String messaggio = utenteService.newUtente(nuovoUtente);
        return new ResponseEntity<>(messaggio, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginDTO, BindingResult checkValidazione) {

        try {

            if (checkValidazione.hasErrors()) {
                StringBuilder erroriValidazione = new StringBuilder("Problemi nella validazione\n");
                for (ObjectError errore : checkValidazione.getAllErrors()) {
                    erroriValidazione.append(errore.getDefaultMessage());
                }

                return new ResponseEntity<>(erroriValidazione.toString(), HttpStatus.BAD_REQUEST);
            }


            LoginResponse response = utenteService.login(loginDTO.getUsername(), loginDTO.getPassword());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Credenziali non valide", HttpStatus.BAD_REQUEST);
        }

    }

    @PatchMapping("/auth/avatar")
    public ResponseEntity<Map<String, String>> cambiaAvatarUtente(
            @RequestPart("avatar") MultipartFile avatar) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println(" Authentication è NULL o non autenticata!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utente non autenticato"));
        }

        String username = authentication.getName();
        System.out.println(" Username autenticato: " + username);

        try {
            Map mappa = cloudinaryConfig.uploader().upload(avatar.getBytes(), ObjectUtils.emptyMap());
            String urlImage = mappa.get("secure_url").toString();
            utenteService.modificaAvatar(username, urlImage);

            Map<String, String> response = new HashMap<>();
            response.put("avatar", urlImage);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento dell'immagine: " + e);
        }
    }





    @GetMapping("/me/info")
    public ResponseEntity<UtenteInfoResponse> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username;
        if (authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            username = authentication.getPrincipal().toString();
        }

        Utente utente = utenteService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        List<EventoDTO> eventiPartecipati = eventoService.getEventiPartecipati(utente.getId());
        List<EventoDTO> eventiCreati = eventoService.getEventiCreati(utente.getId());

        UtenteInfoResponse response = new UtenteInfoResponse();
        response.setId(utente.getId());
        response.setUsername(utente.getUsername());
        response.setEmail(utente.getEmail());
        response.setNome(utente.getNome());
        response.setCognome(utente.getCognome());
        response.setAvatar(utente.getAvatar());
        response.setEventiPartecipati(eventiPartecipati);
        response.setEventiCreati(eventiCreati);

        return ResponseEntity.ok(response);
    }





}