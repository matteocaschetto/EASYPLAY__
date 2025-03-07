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

import com.epicode.EASYPLAY.payload.EventoDTO;
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

    @PostMapping
    public ResponseEntity<EventoDTO> creaEvento(@RequestBody EventoDTO eventoDTO, @RequestHeader("utenteId") Long utenteId) {
        Optional<Utente> creatore = utenteService.findById(utenteId);
        return new ResponseEntity<>(eventoService.creaEvento(eventoDTO, creatore.orElse(null)), HttpStatus.CREATED);
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