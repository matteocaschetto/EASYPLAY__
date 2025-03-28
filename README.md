## EasyPlay - In Campo In Uno Schiocco ⚽️🎾 - Gestione Eventi Sportivi Amatoriali

## Descrizione
EasyPlay è un'applicazione web progettata per semplificare l'organizzazione di partite amatoriali. L'obiettivo principale dell'app è offrire un'esperienza intuitiva per tutti gli utenti, permettendo di creare e partecipare a eventi sportivi in modo rapido e senza complicazioni.

Gli utenti possono registrarsi, accedere alla propria area personale e partecipare a eventi già creati o crearne di nuovi scegliendo lo sport desiderato (calcio ⚽️ o padel 🎾) e l'orario disponibile. La piattaforma permette inoltre di visualizzare gli eventi passati e futuri, offrendo una panoramica chiara delle proprie partecipazioni.

Tutti gli utenti, possono creare eventi, partecipare e gestire la propria area personale. EasyPlay garantisce un'esperienza d'uso ottimale per tutti gli utenti.

## Funzionalità principali:

Registrazione e login per accedere alle funzionalità riservate agli utenti 🔑

Creazione e partecipazione a eventi sportivi (calcio ⚽️ e padel 🎾)

Visualizzazione degli eventi futuri e passati 📅

Gestione utenti con possibilità di modifica del profilo ✏️

Area personale per visualizzare eventi creati e a cui si partecipa 🏠

## Ruolo utente:

USER: Utente che può partecipare agli eventi, creare nuovi eventi e gestire la propria area personale 👤

## Tecnologie utilizzate:

## Frontend:

React con React Router per il routing 🔄

Redux per la gestione dello stato 🛠️

React-Bootstrap per la realizzazione dell'interfaccia utente 🎨

Fetch API per la gestione delle chiamate HTTP 🌐

Stile minimale e intuitivo per un'esperienza utente fluida ✨

## Backend:

Spring Boot per la gestione del server e delle API REST 💻

Spring Security per la gestione dell'autenticazione e delle autorizzazioni 🔐

JWT per la gestione dei token di sessione 🔑

PostgreSQL come database relazionale 🗃️

API REST per la comunicazione tra frontend e backend 🌐

## API principali:

## Autenticazione:

POST /user/new → Registra un nuovo utente (funzionalità di registrazione) 📝

POST /user/login → Effettua il login e restituisce il token di sessione 🔓

PATCH /user/auth/avatar → Cambia l'avatar dell'utente 🖼️

## Eventi:

GET /api/eventi → Recupera tutti gli eventi 📅

GET /api/eventi/{id} → Recupera un evento specifico per ID 📍

POST /api/eventi/new → Crea un nuovo evento 🏟️

PUT /api/eventi/{id} → Modifica un evento esistente ✏️

DELETE /api/eventi/{id} → Elimina un evento ❌

## Prenotazioni:

POST /api/prenotazioni/prenotaPosto → Prenota un posto per un evento 🎟️

DELETE /api/prenotazioni/{id} → Annulla una prenotazione esistente 🚫

## Utente:

GET /user/me/info → Ottieni le informazioni dell'utente autenticato, inclusi gli eventi creati e partecipati 🧑‍💻

Contatti
Email: matteocaschetto9@gmail.com 📧

## Parte Backend

https://github.com/matteocaschetto/EASYPLAY__.git

## Parte Frontend

https://github.com/matteocaschetto/EASYPLAY_FRONTEND.git

