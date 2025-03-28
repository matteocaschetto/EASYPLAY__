

package com.epicode.EASYPLAY.controller;


import com.epicode.EASYPLAY.payload.EventoDTO;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.payload.response.EventoDTOnoid;
import com.epicode.EASYPLAY.repository.UtenteRepository;
import com.epicode.EASYPLAY.service.EventoService;
import com.epicode.EASYPLAY.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/eventi")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping
    public ResponseEntity<List<EventoDTO>> getAllEventi() {
        return new ResponseEntity<>(eventoService.getAllEventi(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> getEventoById(@PathVariable Long id) {
        EventoDTO evento = eventoService.getEventoById(id);
        if (evento == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }


    @PostMapping("/new")
    public ResponseEntity<?> creaEvento(@RequestBody EventoDTOnoid eventoDTO) {
        // Ottenere l'utente autenticato
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Utente non autenticato");
        }

        // Estrarre l'username dal token JWT
        String username = authentication.getName();
        Utente creatore = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Creare l'evento con l'utente autenticato
        EventoDTO eventoCreato = eventoService.creaEvento(eventoDTO, creatore.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoCreato);
    }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminaEvento(@PathVariable Long id, @RequestHeader("utenteId") Long utenteId) {
      System.out.println("🔍 Tentativo di eliminazione evento ID: " + id + " da utente ID: " + utenteId);

      Optional<Utente> utente = utenteService.findById(utenteId);
      if (utente.isEmpty()) {
          System.out.println("⛔ Utente non trovato!");
          return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      eventoService.eliminaEvento(id, utente.get());
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }



    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> modificaEvento(@PathVariable Long id, @RequestBody EventoDTO eventoDTO, @RequestHeader("utenteId") Long utenteId) {
        eventoDTO.setId(id);
        Optional<Utente> utente = utenteService.findById(utenteId);
        return new ResponseEntity<>(eventoService.modificaEvento(eventoDTO, utente.orElse(null)), HttpStatus.OK);
    }


}
