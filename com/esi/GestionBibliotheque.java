package com.esi;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import com.esi.Emprunt.Emprunt;
import com.esi.livre.Livre;
import com.esi.Etudiant.Etudiant;

public class GestionBibliotheque {

    private static final ArrayList<Etudiant> etudiants = new ArrayList<>();
    private static final ArrayList<Livre> livres = new ArrayList<>();
    private static final ArrayList<Emprunt> emprunts = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Gestion de la Bibliothèque ---");
            System.out.println("1. Ajouter un etudiant");
            System.out.println("2. Ajouter un livre");
            System.out.println("3. Afficher les etudiants");
            System.out.println("4. Afficher les livres");
            System.out.println("5. Emprunter un livre");
            System.out.println("6. Afficher les emprunts");
            System.out.println("7. Enregistrer dans un fichier");
            System.out.println("8. Quitter");
            System.out.print("Choisissez une option : ");
            int choix = scanner.nextInt();
            scanner.nextLine(); // Consomme la ligne vide

            switch (choix) {
                case 1: ajouterEtudiant(); break;
                case 2: ajouterLivre(); break;
                case 3: afficherEtudiants(); break;
                case 4: afficherLivres(); break;
                case 5: emprunterLivre(); break;
                case 6: afficherEmprunts(); break;
                case 7: enregistrerDansFichier(); break;
                case 8: {
                    System.out.println("Au revoir !");
                    return;
                }
                default: System.out.println("Option invalide, veuillez réessayer.");
                break;
            }
        }
    }

    private static void ajouterEtudiant() {
        System.out.print("Entrez le nom de l'étudiant : ");
        String nom = scanner.nextLine();
        System.out.print("Entrez le prénom de l'étudiant : ");
        String prenom = scanner.nextLine();
        System.out.print("Entrez l'email de l'étudiant : ");
        String email = scanner.nextLine();
        System.out.print("Entrez l'age de l'étudiant : ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consomme la ligne vide
        System.out.print("Entrez la filière de l'étudiant : ");
        String filiere = scanner.nextLine();
        System.out.print("Entrez l'identifiant de l'étudiant : ");
        String id = scanner.nextLine();
        etudiants.add(new Etudiant(id,nom, prenom, email, age, filiere,));
        System.out.println("Etudiant ajouté avec succès !");
    }

    private static void ajouterLivre() {
        System.out.print("Entrez le titre du livre : ");
        String titre = scanner.nextLine();
        System.out.print("Entrez l'auteur du livre : ");
        String auteur = scanner.nextLine();
        System.out.print("Entrez l'ISBN du livre : ");
        String isbn = scanner.nextLine();
        System.out.print("Entrez l'éditeur du livre : ");
        String editeur = scanner.nextLine();
        System.out.print("Entrez la date de publication (AAAA-MM-JJ) : ");
        LocalDate datePublication = LocalDate.parse(scanner.nextLine());
        System.out.print("Entrez le nombre d'exemplaires disponibles : ");
        int exemplairesDisponibles = scanner.nextInt();
        scanner.nextLine(); // Consomme la ligne vide
        livres.add(new Livre(titre, auteur, exemplairesDisponibles, isbn, editeur, datePublication));
        System.out.println("Livre ajouté avec succès !");
    }

    private static void afficherEtudiants() {
        System.out.println("\n--- Liste des étudiants ---");
        if (etudiants.isEmpty()) {
            System.out.println("Aucun étudiant trouvé.");
        } else {
            for (Etudiant etudiant : etudiants) {
                System.out.println(etudiant);
            }
        }
    }

    private static void afficherLivres() {
        System.out.println("\n--- Liste des livres ---");
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé.");
        } else {
            for (Livre livre : livres) {
                System.out.println(livre);
            }
        }
    }

    private static void emprunterLivre() {
        if (etudiants.isEmpty()) {
            System.out.println("Aucun étudiant n'est enregistré. Veuillez ajouter des étudiants d'abord.");
            return;
        }
        if (livres.isEmpty()) {
            System.out.println("Aucun livre n'est disponible. Veuillez ajouter des livres d'abord.");
            return;
        }

        System.out.println("\n--- Liste des étudiants ---");
        for (int i = 0; i < etudiants.size(); i++) {
            System.out.println((i + 1) + ". " + etudiants.get(i).getNom());
        }
        System.out.print("Sélectionnez un étudiant (entrez le numéro) : ");
        int etudiantIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consomme la ligne vide
        if (etudiantIndex < 0 || etudiantIndex >= etudiants.size()) {
            System.out.println("Etudiant invalide !");
            return;
        }

        System.out.println("\n--- Liste des livres disponibles ---");
        for (int i = 0; i < livres.size(); i++) {
            Livre livre = livres.get(i);
            if (livre.getExemplairesDisponibles() > 0) {
                System.out.println((i + 1) + ". " + livre.getTitre() + " (" + livre.getExemplairesDisponibles() + " exemplaire(s) disponible(s))");
            }
        }
        System.out.print("Sélectionnez un livre à emprunter (entrez le numéro) : ");
        int livreIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consomme la ligne vide
        if (livreIndex < 0 || livreIndex >= livres.size()) {
            System.out.println("Livre invalide !");
            return;
        }

        Livre livreChoisi = livres.get(livreIndex);
        if (livreChoisi.getExemplairesDisponibles() > 0) {
            livreChoisi.setExemplairesDisponibles(livreChoisi.getExemplairesDisponibles() - 1);
            Etudiant etudiant = etudiants.get(etudiantIndex);
            emprunts.add(new Emprunt(etudiant, livreChoisi));
            System.out.println("L'étudiant " + etudiant.getNom() +
                    " a emprunté le livre '" + livreChoisi.getTitre() + "'.");
        } else {
            System.out.println("Aucun exemplaire disponible pour ce livre !");
        }
    }

    private static void afficherEmprunts() {
        System.out.println("\n--- Liste des emprunts ---");
        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt enregistré.");
        } else {
            for (Emprunt emprunt : emprunts) {
                System.out.println(emprunt);
            }
        }
    }

    private static void enregistrerDansFichier() {
        try (FileWriter writer = new FileWriter("bibliotheque.txt")) {
            writer.write("--- Etudiants ---\n");
            for (Etudiant etudiant : etudiants) {
                writer.write(etudiant.toString() + "\n");
            }
            writer.write("\n--- Livres ---\n");
            for (Livre livre : livres) {
                writer.write(livre.toString() + "\n");
            }
            writer.write("\n--- Emprunts ---\n");
            for (Emprunt emprunt : emprunts) {
                writer.write(emprunt.toString() + "\n");
            }
            System.out.println("Données enregistrées dans le fichier 'bibliotheque.txt'.");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'enregistrement dans le fichier : " + e.getMessage());
        }
    }
}