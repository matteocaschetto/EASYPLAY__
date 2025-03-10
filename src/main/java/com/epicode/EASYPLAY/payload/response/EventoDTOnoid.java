package com.epicode.EASYPLAY.payload.response;


import lombok.Data;

import java.util.Date;

@Data
public class EventoDTOnoid {

    private String titolo;
    private String descrizione;
    private Date data;
    private String luogo;
    private int postiDisponibili;
    private int maxPartecipanti;
    private String tipoEvento;

}
