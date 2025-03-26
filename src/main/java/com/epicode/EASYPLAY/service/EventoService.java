
package com.epicode.EASYPLAY.service;

import com.epicode.EASYPLAY.payload.EventoDTO;
import com.epicode.EASYPLAY.model.Evento;
import com.epicode.EASYPLAY.model.Prenotazione;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.payload.response.EventoDTOnoid;
import com.epicode.EASYPLAY.repository.EventoRepository;
import com.epicode.EASYPLAY.repository.PrenotazioneRepository;
import com.epicode.EASYPLAY.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;





    public EventoDTO creaEvento(EventoDTOnoid eventoDTO, Long idUtente) {
        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Evento evento = fromNoIdToEntity(eventoDTO);
        evento.setCreatore(utente); // Assegniamo l'utente autenticato come creatore
        evento = eventoRepository.save(evento);

        return convertToDTO(evento);
    }


    private void impostaMaxPartecipanti(Evento evento) {
        switch (evento.getTipoEvento()) {
            case "calcio a 5":
                evento.setMaxPartecipanti(10);
                break;
            case "calcio a 7":
                evento.setMaxPartecipanti(14);
                break;
            case "calcio a 11":
                evento.setMaxPartecipanti(22);
                break;
            case "padel":
                evento.setMaxPartecipanti(4);
                break;
            default:
                evento.setMaxPartecipanti(0); // O lancia un'eccezione se il tipoEvento non è valido
        }
    }


    public void eliminaEvento(Long id, Utente utente) {
        Evento evento = eventoRepository.findById(id).orElse(null);

        if (evento == null) {
            System.out.println("⛔ Evento non trovato!");
            return;
        }

        if (!evento.getCreatore().equals(utente)) {
            System.out.println("⛔ L'utente non è il creatore di questo evento!");
            return;
        }

        System.out.println("✅ Evento eliminato con successo!");
        eventoRepository.delete(evento);
    }


    public EventoDTO modificaEvento(EventoDTO eventoDTO, Utente utente) {
        Evento eventoEsistente = eventoRepository.findById(eventoDTO.getId()).orElse(null);
        if (eventoEsistente != null && eventoEsistente.getCreatore().equals(utente)) {
            Evento evento = convertToEntity(eventoDTO);
            impostaMaxPartecipanti(evento);
            return convertToDTO(eventoRepository.save(evento));
        }
        return null;
    }

    public List<EventoDTO> getAllEventi() {
        return eventoRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public EventoDTO getEventoById(Long id) {
        return eventoRepository.findById(id).map(this::convertToDTO).orElse(null);
    }


public Prenotazione prenotaPosti(Long eventoId, Long utenteId, int numeroPosti) {
    Evento evento = eventoRepository.findById(eventoId).orElse(null);
    Utente utente = utenteRepository.findById(utenteId).orElse(null);

    if (evento != null && utente != null && evento.getPostiDisponibili() >= numeroPosti) {
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setUtente(utente);
        prenotazione.setEvento(evento);
        prenotazione.setDataPrenotazione(new Date());
        prenotazione.setNumeroPosti(numeroPosti);

        evento.setPostiDisponibili(evento.getPostiDisponibili() - numeroPosti);
        eventoRepository.save(evento);

        return prenotazioneRepository.save(prenotazione);
    }

    return null;
}



    public void annullaPrenotazione(Long prenotazioneId, Long utenteId) {
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata"));

        if (!prenotazione.getUtente().getId().equals(utenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non autorizzato");
        }

        Evento evento = prenotazione.getEvento();
        if (evento == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "L'evento associato non esiste");
        }

        evento.setPostiDisponibili(evento.getPostiDisponibili() + prenotazione.getNumeroPosti());
        eventoRepository.save(evento);

        prenotazioneRepository.delete(prenotazione);
    }


    private EventoDTO convertToDTO(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setTitolo(evento.getTitolo());
        dto.setDescrizione(evento.getDescrizione());
        dto.setData(evento.getData());
        dto.setOrario(evento.getOrario());
        dto.setLuogo(evento.getLuogo());
        dto.setPostiDisponibili(evento.getPostiDisponibili());
        dto.setMaxPartecipanti(evento.getMaxPartecipanti());
        dto.setTipoEvento(evento.getTipoEvento());
        Long creatoreId = evento.getCreatore().getId();
        dto.setCreatoreId(creatoreId);
        return dto;
    }

    private Evento convertToEntity(EventoDTO dto) {
        Evento evento = new Evento();
        evento.setId(dto.getId());
        evento.setTitolo(dto.getTitolo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setData(dto.getData());
        evento.setOrario(dto.getOrario());
        evento.setLuogo(dto.getLuogo());
        evento.setPostiDisponibili(dto.getPostiDisponibili());
        evento.setMaxPartecipanti(dto.getMaxPartecipanti());
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setCreatore(utenteRepository.findById(dto.getCreatoreId()).orElseThrow(() -> new RuntimeException("utente non trovato")));
        return evento;
    }

    private EventoDTOnoid toEventoDTOnoId(EventoDTO dto){
        EventoDTOnoid noId = new EventoDTOnoid();
        noId.setTitolo(dto.getTitolo());
        noId.setDescrizione(dto.getDescrizione());
        noId.setData(dto.getData());
        noId.setLuogo(dto.getLuogo());
        noId.setPostiDisponibili(dto.getPostiDisponibili());
        noId.setMaxPartecipanti(dto.getMaxPartecipanti());
        noId.setTipoEvento(dto.getTipoEvento());
        return  noId;
    }

    private Evento fromNoIdToEntity(EventoDTOnoid noId){


        Evento evento = new Evento();
        evento.setTitolo(noId.getTitolo());
        evento.setDescrizione(noId.getDescrizione());
        evento.setData(noId.getData());
        evento.setOrario(noId.getOrario());
        evento.setLuogo(noId.getLuogo());
        evento.setPostiDisponibili(noId.getPostiDisponibili());
        evento.setMaxPartecipanti(noId.getMaxPartecipanti());
        evento.setTipoEvento(noId.getTipoEvento());


        return evento;
    }

/*
    ----------------------------------
*/

    public List<EventoDTO> getEventiPartecipati(Long utenteId) {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByUtenteId(utenteId);
        return prenotazioni.stream()
                .map(Prenotazione::getEvento)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> getEventiCreati(Long utenteId) {
        List<Evento> eventi = eventoRepository.findByCreatoreId(utenteId);
        return eventi.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}