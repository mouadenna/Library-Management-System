package com.esi.Personne;

public class Personne {
    public String nom;
    protected String prenom;
    protected int age;

    public Personne(String nom, String prenom, int age) {
        this.nom = nom;
        this.prenom=prenom;
        this.age=age;
    }
    public Personne() {}
    
    
    //getters
    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }
    public int getAge() {
    	return age;
    }
    
    
    //setters
    public void setNom(String nom) {
    	this.nom=nom;
    }
    public void setPrenom(String prenom) {
    	this.prenom=prenom;
    }
    public void setAge(int age) {
    	this.age=age;
    }
    

    public String toString() {
        return "Etudiant{" +
                "nom='" + nom + '\'' +
                '}';
    }
}