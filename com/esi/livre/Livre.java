package com.esi.livre;

import java.sql.Date;

public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private int exemplairesDisponibles;
    private String editeur;
    private Date datePublication;

    public Livre(int id,String titre, String auteur, int exemplairesDisponibles, String editeur, Date datePublication) {
    	this.id=id;
        this.titre = titre;
        this.auteur = auteur;
        this.exemplairesDisponibles = exemplairesDisponibles;
        this.setEditeur(editeur);
        this.datePublication = datePublication;
    }



    public Livre() {
	}

	// Essential getters and setters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public int getExemplairesDisponibles() {
        return exemplairesDisponibles;
    }

    public void setExemplairesDisponibles(int exemplairesDisponibles) {
        this.exemplairesDisponibles = exemplairesDisponibles;
    }



    public Date getDatePublication() {
        return datePublication;
    }





    @Override
    public String toString() {
        return String.format("Livre[id=%d, titre='%s', auteur='%s', exemplaires=%d]",
            id, titre, auteur, exemplairesDisponibles);
    }



	public void setAuteur(String auteur) {
		this.auteur=auteur;
		
	}

	public void setTitre(String titre) {
		this.titre=titre;
		
	}

	public String getEditeur() {
		return editeur;
	}

	public void setEditeur(String editeur) {
		this.editeur = editeur;
	}

	public void setDatePublication(Date date) {
		this.datePublication=date;
	}
}