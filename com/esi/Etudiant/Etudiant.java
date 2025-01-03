package com.esi.Etudiant;
import com.esi.Personne.Personne;

public class Etudiant extends Personne {
    private String filiere;
    private String id;
    private String email;
	private String Numero;
    

    // Constructor
    public Etudiant(String id,String nom, String prenom, String email, int age, String filiere,String Numero) {
        super(nom, prenom, age);
        this.email = email;
        this.filiere = filiere;
        this.id = id;
        this.setNumero(Numero);
    }


	//public Etudiant() {
	//}




	public Etudiant() {
		// TODO Auto-generated constructor stub
	}


	// Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


	public String getNumero() {
		return Numero;
	}


	public void setNumero(String numero) {
		Numero = numero;
	}









}