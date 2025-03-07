
package com.epicode.EASYPLAY.service;

import com.epicode.EASYPLAY.payload.EventoDTO;
import com.epicode.EASYPLAY.model.Evento;
import com.epicode.EASYPLAY.model.Prenotazione;
import com.epicode.EASYPLAY.model.Utente;
import com.epicode.EASYPLAY.repository.EventoRepository;
import com.epicode.EASYPLAY.repository.PrenotazioneRepository;
import com.epicode.EASYPLAY.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public EventoDTO creaEvento(EventoDTO eventoDTO, Utente creatore) {
        Evento evento = convertToEntity(eventoDTO, creatore);
        evento.setCreatore(creatore);
        impostaMaxPartecipanti(evento);
        evento.setPostiDisponibili(evento.getMaxPartecipanti());
        return convertToDTO(eventoRepository.save(evento));
    }

    private void impostaMaxPartecipanti(Evento evento) {
        switch (evento.getTipoEvento()) {
            case "calcio5":
                evento.setMaxPartecipanti(10);
                break;
            case "calcio7":
                evento.setMaxPartecipanti(14);
                break;
            case "calcio11":
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
        if (evento != null && evento.getCreatore().equals(utente)) {
            eventoRepository.delete(evento);
        }
    }

    public EventoDTO modificaEvento(EventoDTO eventoDTO, Utente utente) {
        Evento eventoEsistente = eventoRepository.findById(eventoDTO.getId()).orElse(null);
        if (eventoEsistente != null && eventoEsistente.getCreatore().equals(utente)) {
            Evento evento = convertToEntity(eventoDTO, utente);
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
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId).orElse(null);
        if (prenotazione != null && prenotazione.getUtente().getId().equals(utenteId)) {
            Evento evento = prenotazione.getEvento();
            evento.setPostiDisponibili(evento.getPostiDisponibili() + prenotazione.getNumeroPosti());
            eventoRepository.save(evento);
            prenotazioneRepository.delete(prenotazione);
        }
    }

    private EventoDTO convertToDTO(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setTitolo(evento.getTitolo());
        dto.setDescrizione(evento.getDescrizione());
        dto.setData(evento.getData());
        dto.setLuogo(evento.getLuogo());
        dto.setPostiDisponibili(evento.getPostiDisponibili());
        dto.setMaxPartecipanti(evento.getMaxPartecipanti());
        dto.setTipoEvento(evento.getTipoEvento());
        dto.setCreatoreId(evento.getCreatore().getId());
        return dto;
    }

    private Evento convertToEntity(EventoDTO dto, Utente utente) {
        Evento evento = new Evento();
        evento.setId(dto.getId());
        evento.setTitolo(dto.getTitolo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setData(dto.getData());
        evento.setLuogo(dto.getLuogo());
        evento.setPostiDisponibili(dto.getPostiDisponibili());
        evento.setMaxPartecipanti(dto.getMaxPartecipanti());
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setCreatore(utente);
        return evento;
    }
}