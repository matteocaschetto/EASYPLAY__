package com.epicode.EASYPLAY.controller;

import com.epicode.EASYPLAY.model.Prenotazione;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.payload.PrenotazioneDTO;
import com.epicode.EASYPLAY.repository.UtenteRepository;
import com.epicode.EASYPLAY.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private UtenteRepository utenteRepository;



    @PostMapping("/prenotaPosto")
    public ResponseEntity<?> prenotaPosto(@RequestBody PrenotazioneDTO prenotazioneDTO,
                                          @AuthenticationPrincipal String username) {
        Utente utente = utenteRepository.findByUsername(username).orElse(null);
        if (utente == null) {
            return new ResponseEntity<>("Utente non trovato", HttpStatus.UNAUTHORIZED);
        }

        Prenotazione prenotazione = eventoService.prenotaPosti(
                prenotazioneDTO.getEventoId(),
                utente.getId(),
                prenotazioneDTO.getNumeroPosti()
        );

        return ResponseEntity.ok(prenotazione);
    }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> annullaPrenotazione(@PathVariable Long id, Authentication authentication) {
      if (authentication == null || !authentication.isAuthenticated()) {
          return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      System.out.println("✅ Autenticazione trovata: " + authentication);

      String username = authentication.getName(); // Ottieni lo username
      Utente utenteAutenticato = utenteRepository.findByUsername(username)
              .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non trovato"));

      System.out.println("✅ Utente autenticato: " + utenteAutenticato.getUsername());

      eventoService.annullaPrenotazione(id, utenteAutenticato.getId());
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


}
