package com.epicode.EASYPLAY.payload;

import lombok.Data;
import java.util.List;

@Data
public class UtenteInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String nome;
    private String cognome;
    private String avatar;
    private List<EventoDTO> eventiPartecipati;
    private List<EventoDTO> eventiCreati;
}
