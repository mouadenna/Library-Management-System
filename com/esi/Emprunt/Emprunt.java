package com.esi.Emprunt;

import java.time.LocalDate;
import java.sql.Date;

import com.esi.Etudiant.Etudiant;
import com.esi.livre.Livre;



public class Emprunt {
    private Etudiant etudiant; // The student who borrowed the book
    private Livre livre;       // The book that was borrowed
    private LocalDate dateEmprunt; // The date the book was borrowed
    private LocalDate dateRetourPrevue;
    private Date dateRetourEffective;
    private int id;

	private Status Status;
	public enum Status {
	    RETOURNE("Retourné"),
	    EN_COURS("En cours");

	    private final String displayName;

	    Status(String displayName) {
	        this.displayName = displayName;
	    }

	    public String getDisplayName() {
	        return displayName;
	    }

	    public static Status fromDisplayName(String displayName) {
	        for (Status status : values()) {
	            if (status.displayName.equals(displayName)) {
	                return status;
	            }
	        }
	        throw new IllegalArgumentException("Unknown status: " + displayName);
	    }
	}




    public Emprunt(Etudiant etudiant, Livre livreChoisi) {
        if (etudiant == null || livreChoisi == null) {
            throw new IllegalArgumentException("Vous devez preciser l'etudiant et le livre");
        }
        this.id=0;
        this.etudiant = etudiant;
        this.livre = livreChoisi;
        this.dateEmprunt = LocalDate.now(); // Set the borrowing date to the current date
        this.dateRetourEffective=null;
        this.dateRetourPrevue=LocalDate.now().plusDays(14);
        this.Status=Status.EN_COURS;
    }
    public Emprunt() {
    	this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue=LocalDate.now().plusDays(14);
    	this.dateRetourEffective=null;
        this.Status=Status.EN_COURS;

    }


	public Livre getLivre() {
        return livre;
    }


    public Etudiant getEtudiant() {
        return etudiant;
    }


    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    // Setters

    public void setLivre(Livre livre) {
        if (livre == null) {
            throw new IllegalArgumentException("Livre must not be null.");
        }
        this.livre = livre;
    }


    public void setEtudiant(Etudiant etudiant) {
        if (etudiant == null) {
            throw new IllegalArgumentException("Etudiant must not be null.");
        }
        this.etudiant = etudiant;
    }


    public void setDateEmprunt(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("DateEmprunt must not be null.");
        }
        this.dateEmprunt = date;
    }
	public int getId() {
		
		return this.id;
	}
	public void setId(int id) {
		this.id=id;
	}
	public Status getStatut() {
		return Status;
	}
	public void setStatut(Status Status) {
		this.Status = Status;
	}
	public void markAsReturned() {
		Status= Status.RETOURNE;
	}
	public void setDateRetourEffective(Date dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

	public Date getDateRetourEffective() {
		return dateRetourEffective;
	}

	public LocalDate getDateRetourPrevue() {
		return dateRetourPrevue;
	}
	public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
		this.dateRetourPrevue = dateRetourPrevue;
	}
	





}