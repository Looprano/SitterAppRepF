package it.unito.di.taass.helpin.oggetti;

/**
 * Classe che contiene l'oggetto "Recensione"
 */

public class Recensione {
    String username;
    String descrizione;
    Float rating;

    //castruttore
    public Recensione(String username, String descrizione, Float rating) {
        this.username = username;
        this.descrizione = descrizione;
        this.rating = rating;
    }

    //metodi get e set
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
