package com.epicode.EASYPLAY.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    @Column(nullable = false)
    private String descrizione;

    @Column(nullable = false)
    private Date data;

    @Column(nullable = false)
    private LocalTime orario;

    @Column(nullable = false)
    private String luogo;

    @Column(nullable = false)
    private int postiDisponibili;

    @Column(nullable = false)
    private int maxPartecipanti;

    @Column(nullable = false)
    private String tipoEvento; // "calcio5", "calcio7", "calcio11", "padel"

    @ManyToOne
    @JoinColumn(name = "creatore_id")
    private Utente creatore;
}