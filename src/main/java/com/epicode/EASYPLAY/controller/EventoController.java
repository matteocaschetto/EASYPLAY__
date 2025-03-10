/*
package com.epicode.EASYPLAY.controller;


import com.epicode.EASYPLAY.model.Evento;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.service.EventoService;
import com.epicode.EASYPLAY.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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



    @PostMapping
    public ResponseEntity<Evento> creaEvento(@RequestBody Evento evento, @RequestHeader("utenteId") Long utenteId) {
        Optional<Utente> creatore = utenteService.findById(utenteId);
        Evento nuovoEvento = eventoService.creaEvento(evento, creatore.orElse(null));
        return new ResponseEntity<>(nuovoEvento, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaEvento(@PathVariable Long id, @RequestHeader("utenteId") Long utenteId) {
        Optional<Utente> utente = utenteService.findById(id);
        eventoService.eliminaEvento(id, utente.orElse(null));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> modificaEvento(@PathVariable Long id, @RequestBody Evento evento, @RequestHeader("utenteId") Long utenteId) {
        Optional<Utente> utente = utenteService.findById(id);
        Evento eventoModificato = eventoService.modificaEvento(evento, utente.orElse(null));
        return new ResponseEntity<>(eventoModificato, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Evento>> getAllEventi() {
        List<Evento> eventi = eventoService.getAllEventi();
        return new ResponseEntity<>(eventi, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getEventoById(@PathVariable Long id) {
        Evento evento = eventoService.getEventoById(id);
        if (evento == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }
}*/

package com.epicode.EASYPLAY.controller;

import com.epicode.EASYPLAY.model.Evento;
import com.epicode.EASYPLAY.payload.EventoDTO;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.payload.response.EventoDTOnoid;
import com.epicode.EASYPLAY.repository.UtenteRepository;
import com.epicode.EASYPLAY.service.EventoService;
import com.epicode.EASYPLAY.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

   /* @PostMapping("/new/{utenteId}")
    public ResponseEntity<?> creaEvento(@RequestBody EventoDTOnoid eventoDTO, @PathVariable Long utenteId) {

        Utente creatore = utenteService.findById(utenteId).orElseThrow(() -> new RuntimeException("utente non trovato"));
        EventoDTO evento = eventoService.creaEvento(eventoDTO, creatore.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }*/

    @PostMapping("/new/{utenteId}")
    public ResponseEntity<?> creaEvento(@RequestBody EventoDTOnoid eventoDTO,
                                        @PathVariable Long utenteId) {
        if (utenteId != null) {
            Optional<Utente> utente = utenteRepository.findById(utenteId);
            // altre operazioni
        } else {
            // Gestisci il caso in cui l'ID sia nullo
            throw new IllegalArgumentException("L'ID non può essere nullo.");
        }
        Utente creatore = utenteService.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("utente non trovato"));
        EventoDTO evento = eventoService.creaEvento(eventoDTO, creatore.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaEvento(@PathVariable Long id, @RequestHeader("utenteId") Long utenteId) {
        Optional<Utente> utente = utenteService.findById(utenteId);
        eventoService.eliminaEvento(id, utente.orElse(null));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> modificaEvento(@PathVariable Long id, @RequestBody EventoDTO eventoDTO, @RequestHeader("utenteId") Long utenteId) {
        eventoDTO.setId(id);
        Optional<Utente> utente = utenteService.findById(utenteId);
        return new ResponseEntity<>(eventoService.modificaEvento(eventoDTO, utente.orElse(null)), HttpStatus.OK);
    }

    @PostMapping("/{eventoId}/prenota")
    public ResponseEntity<?> prenotaPosti(@PathVariable Long eventoId, @RequestHeader("utenteId") Long utenteId, @RequestParam int numeroPosti) {
        if(numeroPosti <= 0){
            return ResponseEntity.badRequest().body("Il numero dei posti deve essere maggiore di zero.");
        }

        return Optional.ofNullable(eventoService.prenotaPosti(eventoId, utenteId, numeroPosti))
                .map(prenotazione -> ResponseEntity.ok("Prenotazione effettuata con successo."))
                .orElse(ResponseEntity.badRequest().body("Impossibile effettuare la prenotazione. Controlla i posti disponibili o l'esistenza dell'evento."));
    }

    @DeleteMapping("/prenotazioni/{prenotazioneId}")
    public ResponseEntity<Void> annullaPrenotazione(@PathVariable Long prenotazioneId, @RequestHeader("utenteId") Long utenteId) {
        eventoService.annullaPrenotazione(prenotazioneId, utenteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}