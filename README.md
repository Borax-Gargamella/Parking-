# Parking-

Parking- è un'applicazione Android progettata per la gestione intelligente dei parcheggi. Permette agli utenti di trovare, prenotare e gestire posti auto in modo semplice tramite un'interfaccia mobile moderna, integrandosi con Firebase per la memorizzazione dei dati.

## Funzionalità principali

- **Gestione Utenti:** Registrazione, aggiornamento e gestione dei dati utente.
- **Gestione Parcheggi:** Creazione, modifica ed eliminazione di parcheggi, con possibilità di associare più posti auto a ciascun parcheggio.
- **Gestione Posti Auto:** Aggiunta, modifica, eliminazione e stato di occupazione dei posti auto.
- **Storico Prenotazioni:** Tracciamento dello storico delle prenotazioni dei posti auto, con gestione delle prenotazioni attive, chiuse e pagamenti.
- **Gestione Luoghi e Zone:** Categorizzazione dei parcheggi per luogo e suddivisione in zone.
- **Integrazione Firebase:** Utilizzo di Firebase Firestore per la persistenza dei dati in tempo reale.

## Struttura del progetto

- `app/src/main/java/com/contest/parking/data/repository/`  
  Contiene i repository per l'accesso e la gestione dei dati relativi a utenti, parcheggi, posti auto, storico, luoghi e zone.

- `app/src/main/java/com/contest/parking/domain/`  
  Include i casi d'uso per la logica di business, come l'aggiornamento dei dati utente.

## Tecnologie utilizzate

- **Linguaggio:** Java
- **Database:** Firebase Firestore
- **Android SDK**

## Licenza

Questo progetto è rilasciato sotto licenza GPL.

---
