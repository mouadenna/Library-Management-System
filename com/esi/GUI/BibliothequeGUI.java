package com.esi.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.esi.livre.Livre;
import com.esi.Etudiant.Etudiant;
import com.esi.Emprunt.Emprunt;
import com.esi.dao.*;

public class BibliothequeGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel livresPanel, etudiantsPanel, empruntsPanel;
    private JTable livresTable, etudiantsTable, empruntsTable;
    
    private LivreDAO livreDAO;
    private EtudiantDAO etudiantDAO;
    private EmpruntDAO empruntDAO;
    private boolean AfficherTout = false; // Variable to store the checkbox state



    // Custom colors
    private static final Color PRIMARY_COLOR = new Color(51, 105, 232);
    private static final Color SECONDARY_COLOR = new Color(245, 247, 250);
    private static final Color ACCENT_COLOR = new Color(50, 50, 200);
    private static final Color TEXT_COLOR = new Color(0, 0, 0);
    private static final Color OVERDUE_COLOR = new Color(255, 200, 200); // Light red
    
    public BibliothequeGUI() throws SQLException {
        setTitle("Système de Gestion de Bibliothèque");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(SECONDARY_COLOR);

        // Initialize DAOs
        livreDAO = new LivreDAOImpl();
        etudiantDAO = new EtudiantDAOImpl();
        empruntDAO = new EmpruntDAOImpl();

        initComponents();
        customizeUI();
    }

    private void customizeUI() {
        // Customize JTabbedPane
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        //tabbedPane.setForegroundAt(1, PRIMARY_COLOR);

    }

    private void initComponents() throws SQLException {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);

        // Initialize panels with custom styling
        livresPanel = createLivresPanel();
        etudiantsPanel = createEtudiantsPanel();
        empruntsPanel = createEmpruntsPanel();

        // Add panels to tabbed pane
        tabbedPane.addTab("Livres", livresPanel);
        tabbedPane.addTab("Étudiants", etudiantsPanel);
        tabbedPane.addTab("Emprunts", empruntsPanel);

        add(tabbedPane);
    }
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Rechercher:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField searchField = new JTextField(27);
        searchField.setPreferredSize(new Dimension(200, 35));
        
        JButton searchButton = createStyledButton("Rechercher", ACCENT_COLOR);
        
        searchButton.addActionListener(e -> {
            searchItems(searchField.getText());
        });        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private void searchItems(String searchTerm) {
        System.out.println("Content:" + searchTerm);

        switch (tabbedPane.getSelectedIndex()) {
            case 0: // Books tab
                searchLivres(searchTerm);
                break;
            case 1: // Students tab
                searchEtudiants(searchTerm);
                break;
            case 2: // Loans tab
                searchEmprunts(searchTerm);
                break;
        }
    }

    private JPanel createLivresPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search panel with modern design
        JPanel searchPanel = createSearchPanel();
        


        // Table with custom styling
        JPanel tablePanel = createTablePanel();
        String[] columns = {"ID", "Titre", "Auteur", "Editeur","DatePublication","Exemplaires Disponible"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        livresTable = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(livresTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons panel
        JPanel buttonsPanel = createButtonsPanel(
            new String[]{"Ajouter", "Modifier", "Supprimer"},
            new ActionListener[]{
                e -> showAddLivreDialog(),
                e -> showEditLivreDialog(),
                e -> deleteLivre()
            }
        );

        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        populateLivresTable();
        return panel;
    }

    private void showAddLivreDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un Livre", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Adjust the GridLayout to have only 2 columns (one for labels and one for input fields)
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));  // 6 rows, 2 columns
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField titreField = new JTextField();
        JTextField auteurField = new JTextField();
        JTextField isbnField = new JTextField();
        JSpinner exemplairesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JTextField editeurField = new JTextField(20);
        JSpinner datePubSpinner = new JSpinner(new SpinnerDateModel());  // Assuming you're using a JSpinner for date picker

        inputPanel.add(new JLabel("Titre:"));
        inputPanel.add(titreField);
        inputPanel.add(new JLabel("Auteur:"));
        inputPanel.add(auteurField);
        inputPanel.add(new JLabel("Exemplaires:"));
        inputPanel.add(exemplairesSpinner);
        inputPanel.add(new JLabel("Editeur:")); 
        inputPanel.add(editeurField); 
        inputPanel.add(new JLabel("Date de Publication:")); 
        inputPanel.add(datePubSpinner);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Enregistrer", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Annuler", Color.GRAY);

        saveButton.addActionListener(e -> {
            try {

                // Check if the titreField is filled
                if (titreField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Le titre est obligatoire.");
                    return;
                }

                Livre livre = new Livre();
                livre.setTitre(titreField.getText());
                livre.setAuteur(auteurField.getText());
                livre.setExemplairesDisponibles((Integer) exemplairesSpinner.getValue());
                livre.setEditeur(editeurField.getText());
                

                // Get the date from the spinner and handle conversion
                Object selectedDate = datePubSpinner.getValue();
                if (selectedDate instanceof java.util.Date) {
                    java.util.Date utilDate = (java.util.Date) selectedDate;
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    livre.setDatePublication(sqlDate);
                } else {
                    JOptionPane.showMessageDialog(this, "Date de publication invalide.");
                    return;
                }

                // Debugging: Print out the livre object to check if it's populated correctly
                System.out.println("Livre: " + livre);

                livreDAO.enregistrerLivre(livre);
                populateLivresTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Livre ajouté avec succès!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private void showEditLivreDialog() {
        int selectedRow = livresTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à modifier.");
            return;
        }

        int livreId = (Integer) livresTable.getValueAt(selectedRow, 0);
        try {
            Livre livre = livreDAO.getLivreById(livreId);
            JDialog dialog = new JDialog(this, "Modifier un Livre", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JTextField titreField = new JTextField(livre.getTitre());
            JTextField auteurField = new JTextField(livre.getAuteur());
            JTextField EditeurField = new JTextField(livre.getEditeur());
            JSpinner exemplairesSpinner = new JSpinner(new SpinnerNumberModel(
                livre.getExemplairesDisponibles(), 1, 100, 1));
            JSpinner datePubSpinner = new JSpinner(new SpinnerDateModel());  // Assuming you're using a JSpinner for date picker

            
            inputPanel.add(new JLabel("Titre:"));
            inputPanel.add(titreField);
            inputPanel.add(new JLabel("Auteur:"));
            inputPanel.add(auteurField);
            inputPanel.add(new JLabel("Editeur:"));
            inputPanel.add(EditeurField);
            inputPanel.add(new JLabel(" Date de publication:"));
            inputPanel.add(datePubSpinner);
            inputPanel.add(new JLabel("Exemplaires:"));
            inputPanel.add(exemplairesSpinner);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = createStyledButton("Enregistrer", PRIMARY_COLOR);
            JButton cancelButton = createStyledButton("Annuler", Color.GRAY);

            saveButton.addActionListener(e -> {
                try {
                    livre.setTitre(titreField.getText());
                    livre.setAuteur(auteurField.getText());
                    livre.setExemplairesDisponibles((Integer) exemplairesSpinner.getValue());
                    
                    livreDAO.updateLivre(livre);
                    populateLivresTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Livre modifié avec succès!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(inputPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du livre: " + ex.getMessage());
        }
    }

    private void deleteLivre() {
        int selectedRow = livresTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre à supprimer.");
            return;
        }

        int livreId = (Integer) livresTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer ce livre?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                livreDAO.deleteLivre(livreId);
                populateLivresTable();
                JOptionPane.showMessageDialog(this, "Livre supprimé avec succès!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage());
            }
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////

	private JPanel createEtudiantsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search panel
        JPanel searchPanel = createSearchPanel();

        // Table panel
        JPanel tablePanel = createTablePanel();
        String[] columns = {"ID", "Nom", "Prénom", "Numéro Étudiant", "Email"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        etudiantsTable = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(etudiantsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = createButtonsPanel(
            new String[]{"Ajouter", "Modifier", "Supprimer"},
            new ActionListener[]{
                e -> showAddEtudiantDialog(),
                e -> showEditEtudiantDialog(),
                e -> deleteEtudiant()
            }
        );
        
        

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        populateEtudiantsTable();
        return panel;
    }


	
	
	private void showAddEtudiantDialog() {
		
		
	    JDialog dialog = new JDialog(this, "Ajouter un Étudiant", true);
	    dialog.setLayout(new BorderLayout(10, 10));
	    dialog.setSize(400, 350);
	    dialog.setLocationRelativeTo(this);

	    JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
	    inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    JTextField idField = new JTextField();
	    JTextField nomField = new JTextField();
	    JTextField prenomField = new JTextField();
	    JTextField emailField = new JTextField();
	    JTextField numeroField = new JTextField();


	    JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(18, 16, 100, 1));
	    JTextField filiereField = new JTextField();

	    inputPanel.add(new JLabel("ID:"));
	    inputPanel.add(idField);
	    inputPanel.add(new JLabel("Nom:"));
	    inputPanel.add(nomField);
	    inputPanel.add(new JLabel("Prénom:"));
	    inputPanel.add(prenomField);
	    inputPanel.add(new JLabel("Email:"));
	    inputPanel.add(emailField);
	    inputPanel.add(new JLabel("Numero telephone:"));
	    inputPanel.add(numeroField);
	    inputPanel.add(new JLabel("Age:"));
	    inputPanel.add(ageSpinner);
	    inputPanel.add(new JLabel("Filière:"));
	    inputPanel.add(filiereField);

	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    JButton saveButton = createStyledButton("Enregistrer", PRIMARY_COLOR);
	    JButton cancelButton = createStyledButton("Annuler", Color.GRAY);

	    saveButton.addActionListener(e -> {
	        try {
	            Etudiant etudiant = new Etudiant();
	            etudiant.setId(idField.getText());
	            etudiant.setNom(nomField.getText());
	            etudiant.setPrenom(prenomField.getText());
	            etudiant.setEmail(emailField.getText());
	            etudiant.setNumero(numeroField.getText());
	            etudiant.setAge((Integer) ageSpinner.getValue());
	            etudiant.setFiliere(filiereField.getText());

	            etudiantDAO.enregistrerEtudiant(etudiant);
	            populateEtudiantsTable();
	            dialog.dispose();
	            JOptionPane.showMessageDialog(this, "Étudiant ajouté avec succès!");
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout: " + ex.getMessage());
	        }
	    });

	    cancelButton.addActionListener(e -> dialog.dispose());

	    buttonPanel.add(saveButton);
	    buttonPanel.add(cancelButton);

	    dialog.add(inputPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);
	    dialog.setVisible(true);
	}

	private void showEditEtudiantDialog() {
	    int selectedRow = etudiantsTable.getSelectedRow();
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant à modifier.");
	        return;
	    }

	    String etudiantId = (String) etudiantsTable.getValueAt(selectedRow, 0);
	    try {
	        Etudiant etudiant = etudiantDAO.getEtudiantById(etudiantId);
	        JDialog dialog = new JDialog(this, "Modifier un Étudiant", true);
	        dialog.setLayout(new BorderLayout(10, 10));
	        dialog.setSize(400, 300);
	        dialog.setLocationRelativeTo(this);

	        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
	        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

	        JTextField nomField = new JTextField(etudiant.getNom());
	        JTextField prenomField = new JTextField(etudiant.getPrenom());
	        JTextField numeroField = new JTextField(etudiant.getNumero());
	        JTextField emailField = new JTextField(etudiant.getEmail());
	        JTextField filiereField= new  JTextField(etudiant.getFiliere());
	        SpinnerNumberModel ageModel = new SpinnerNumberModel(etudiant.getAge(), 0, 120, 1); 
	        JSpinner ageSpinner = new JSpinner(ageModel);

	        inputPanel.add(new JLabel("Nom:"));
	        inputPanel.add(nomField);
	        inputPanel.add(new JLabel("Prénom:"));
	        inputPanel.add(prenomField);
	        inputPanel.add(new JLabel("Numéro Étudiant:"));
	        inputPanel.add(numeroField);
	        inputPanel.add(new JLabel("Filiere Étudiant:"));
	        inputPanel.add(filiereField);
	        inputPanel.add(new JLabel("Email:"));
	        inputPanel.add(emailField);
	        inputPanel.add(new JLabel("Age:"));
	        inputPanel.add(ageSpinner);

	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	        JButton saveButton = createStyledButton("Enregistrer", PRIMARY_COLOR);
	        JButton cancelButton = createStyledButton("Annuler", Color.GRAY);

	        saveButton.addActionListener(e -> {
	            try {
	                etudiant.setNom(nomField.getText());
	                etudiant.setPrenom(prenomField.getText());
	                etudiant.setNumero(numeroField.getText());
	                etudiant.setFiliere(filiereField.getText());
	                etudiant.setEmail(emailField.getText());
	                etudiant.setAge((int) ageSpinner.getValue());
	                
	                etudiantDAO.updateEtudiant(etudiant);
	                populateEtudiantsTable();
	                dialog.dispose();
	                JOptionPane.showMessageDialog(this, "Étudiant modifié avec succès!");
	            } catch (SQLException ex) {
	                JOptionPane.showMessageDialog(this, "Erreur lors de la modification: " + ex.getMessage());
	            }
	        });

	        cancelButton.addActionListener(e -> dialog.dispose());

	        buttonPanel.add(saveButton);
	        buttonPanel.add(cancelButton);

	        dialog.add(inputPanel, BorderLayout.CENTER);
	        dialog.add(buttonPanel, BorderLayout.SOUTH);
	        dialog.setVisible(true);

	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(this, "Erreur lors de la récupération de l'étudiant: " + ex.getMessage());
	    }
	}

	private void deleteEtudiant() {
	    int selectedRow = etudiantsTable.getSelectedRow();
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant à supprimer.");
	        return;
	    }

	    String etudiantId = (String) etudiantsTable.getValueAt(selectedRow, 0);
	    int confirm = JOptionPane.showConfirmDialog(
	        this,
	        "Êtes-vous sûr de vouloir supprimer cet étudiant?",
	        "Confirmation",
	        JOptionPane.YES_NO_OPTION
	    );

	    if (confirm == JOptionPane.YES_OPTION) {
	        try {
	            etudiantDAO.deleteEtudiant(etudiantId);
	            populateEtudiantsTable();
	            JOptionPane.showMessageDialog(this, "Étudiant supprimé avec succès!");
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression: " + ex.getMessage());
	        }
	    }
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////

	private JPanel createEmpruntsPanel() throws SQLException {
	    JPanel panel = new JPanel(new BorderLayout(10, 10));
	    panel.setBackground(Color.WHITE);
	    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

	    // Create a container panel for search and emprunt view with GridBagLayout
	    JPanel topPanel = new JPanel(new GridBagLayout());
	    topPanel.setBackground(Color.WHITE); // Set background color to white
	    GridBagConstraints gbc = new GridBagConstraints();

	    // Set the column weights (70% for the first column, 30% for the second)
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.weightx = 0.7; // 70% for the first column
	    gbc.gridx = 0;
	    JPanel searchPanel = createSearchPanel();
	    topPanel.add(searchPanel, gbc);

	    gbc.weightx = 0.3; // 30% for the second column
	    gbc.gridx = 1;
	    JPanel empruntViewPanel = createEmpruntViewPanel();
	    topPanel.add(empruntViewPanel, gbc);

	    // Table panel
	    JPanel tablePanel = createTablePanel();
	    String[] columns = {"ID", "Étudiant", "Livre", "Date Emprunt", "Date Retour Prévue", "Date Retour Effective", "Statut"};
	    DefaultTableModel model = new DefaultTableModel(columns, 0);
	    empruntsTable = createStyledTable(model);

	    // Custom renderer for all columns to handle row coloring
	    empruntsTable.setDefaultRenderer(Object.class, new TableCellRenderer() {
	        @Override
	        public Component getTableCellRendererComponent(JTable table, Object value,
	                boolean isSelected, boolean hasFocus, int row, int column) {

	            JLabel label = new JLabel();
	            label.setOpaque(true);

	            // Format the value
	            if (value != null) {
	                label.setText(value.toString());
	            }

	            // Get the due date from column 4 ("Date Retour Prévue")
	            LocalDate dueDate = (LocalDate) table.getValueAt(row, 4);

	            if (LocalDate.now().isAfter(dueDate)) {
	                label.setBackground(OVERDUE_COLOR);
	            } else {
	                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
	            }

	            // Handle selection highlighting
	            if (isSelected) {
	                label.setBackground(table.getSelectionBackground());
	                label.setForeground(table.getSelectionForeground());
	            }

	            // Center align the content
	            label.setHorizontalAlignment(SwingConstants.CENTER);

	            return label;
	        }
	    });

	    JScrollPane scrollPane = new JScrollPane(empruntsTable);
	    scrollPane.setBorder(BorderFactory.createEmptyBorder());
	    tablePanel.add(scrollPane, BorderLayout.CENTER);

	    // Buttons panel
	    JPanel buttonsPanel = createButtonsPanel(
	        new String[]{"Nouvel Emprunt", "Retourner"},
	        new ActionListener[]{
	            e -> showNewEmpruntDialog(),
	            e -> returnLivre()
	        }
	    );

	    panel.add(topPanel, BorderLayout.NORTH);
	    panel.add(tablePanel, BorderLayout.CENTER);
	    panel.add(buttonsPanel, BorderLayout.SOUTH);

	    populateEmpruntsTable();
	    return panel;
	}



	private void showNewEmpruntDialog() {
	    JDialog dialog = new JDialog(this, "Nouvel Emprunt", true);
	    dialog.setLayout(new BorderLayout(10, 10));
	    dialog.setSize(400, 300);
	    dialog.setLocationRelativeTo(this);

	    JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
	    inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    // Custom renderer for Livre items
	    JComboBox<Livre> livreCombo = new JComboBox<>();
	    livreCombo.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(JList<?> list, Object value, 
	                int index, boolean isSelected, boolean cellHasFocus) {
	            if (value instanceof Livre) {
	                Livre livre = (Livre) value;
	                value = livre.getTitre() + " (" + livre.getExemplairesDisponibles() + " disponibles)";
	            }
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });

	    // Custom renderer for Etudiant items
	    JComboBox<Etudiant> etudiantCombo = new JComboBox<>();
	    etudiantCombo.setRenderer(new DefaultListCellRenderer() {
	        @Override
	        public Component getListCellRendererComponent(JList<?> list, Object value, 
	                int index, boolean isSelected, boolean cellHasFocus) {
	            if (value instanceof Etudiant) {
	                Etudiant etudiant = (Etudiant) value;
	                value = etudiant.getNom() + " " + etudiant.getPrenom();
	            }
	            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	        }
	    });

	    try {
	        for (Etudiant etudiant : etudiantDAO.getTousLesEtudiants()) {
	            etudiantCombo.addItem(etudiant);
	        }
	        for (Livre livre : livreDAO.getTousLesLivres()) {
	            if (livre.getExemplairesDisponibles() > 0) {
	                livreCombo.addItem(livre);
	            }
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données: " + ex.getMessage());
	        return;
	    }

	    // Set default selections if items exist
	    if (etudiantCombo.getItemCount() > 0) {
	        etudiantCombo.setSelectedIndex(0);
	    }
	    if (livreCombo.getItemCount() > 0) {
	        livreCombo.setSelectedIndex(0);
	    }

	    inputPanel.add(new JLabel("Étudiant:"));
	    inputPanel.add(etudiantCombo);
	    inputPanel.add(new JLabel("Livre:"));
	    inputPanel.add(livreCombo);

	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	    JButton saveButton = createStyledButton("Enregistrer", PRIMARY_COLOR);
	    JButton cancelButton = createStyledButton("Annuler", Color.GRAY);

	    saveButton.addActionListener(e -> {
	        if (etudiantCombo.getSelectedItem() == null || livreCombo.getSelectedItem() == null) {
	            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant et un livre");
	            return;
	        }

	        try {
	            Emprunt emprunt = new Emprunt();
	            emprunt.setEtudiant((Etudiant) etudiantCombo.getSelectedItem());
	            emprunt.setLivre((Livre) livreCombo.getSelectedItem());
	            emprunt.setDateEmprunt(LocalDate.now());
	            emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14)); // 2 weeks loan period
	            emprunt.setStatut(Emprunt.Status.EN_COURS);

	            // Update book availability
	            Livre livre = emprunt.getLivre();
	            livre.setExemplairesDisponibles(livre.getExemplairesDisponibles() - 1);
	            livreDAO.updateLivre(livre);

	            // Save the loan
	            empruntDAO.enregistrerEmprunt(emprunt);

	            populateEmpruntsTable();
	            populateLivresTable();
	            dialog.dispose();
	            JOptionPane.showMessageDialog(this, "Emprunt enregistré avec succès!");
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement: " + ex.getMessage());
	        }
	    });

	    cancelButton.addActionListener(e -> dialog.dispose());

	    buttonPanel.add(saveButton);
	    buttonPanel.add(cancelButton);

	    dialog.add(inputPanel, BorderLayout.CENTER);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);
	    dialog.setVisible(true);
	}
	private void returnLivre() {
	    int selectedRow = empruntsTable.getSelectedRow();
	    if (selectedRow == -1) {
	        JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à retourner.");
	        return;
	    }

	    int empruntId = (Integer) empruntsTable.getValueAt(selectedRow, 0);
	    try {
	        Emprunt emprunt = empruntDAO.getEmpruntById(empruntId);
	        
	        if (emprunt.getStatut().equals("Retourné")) {
	            JOptionPane.showMessageDialog(this, "Ce livre a déjà été retourné.");
	            return;
	        }

	        int confirm = JOptionPane.showConfirmDialog(
	            this,
	            "Confirmer le retour du livre?",
	            "Confirmation",
	            JOptionPane.YES_NO_OPTION
	        );

	        if (confirm == JOptionPane.YES_OPTION) {
	            // Update loan status
	            emprunt.markAsReturned();
	            emprunt.setDateRetourEffective(java.sql.Date.valueOf(LocalDate.now()));
	            System.out.println(emprunt.getDateRetourEffective());

	            empruntDAO.modifierEmprunt(emprunt);

	            // Update book availability
	            Livre livre = emprunt.getLivre();
	            livre.setExemplairesDisponibles(livre.getExemplairesDisponibles() + 1);
	            livreDAO.updateLivre(livre);

	            // Refresh tables
	            populateEmpruntsTable();
	            populateLivresTable();

	            JOptionPane.showMessageDialog(this, "Livre retourné avec succès!");
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(this, 
	            "Erreur lors du retour du livre: " + ex.getMessage(),
	            "Erreur",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	}
	/////////////////////////////////////////////////////////////////////////////////////////////
	

	// Helper method to format dates
	private String formatDate(LocalDate localDate) {
	    if (localDate == null) return "";
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    return sdf.format(localDate);
	}




	private void searchLivres(String searchTerm) {
		
	    DefaultTableModel model = (DefaultTableModel) livresTable.getModel();
	    model.setRowCount(0);
	    
	    try {
	        for (Livre livre : livreDAO.getTousLesLivres()) {
	            if (livre.getTitre().toLowerCase().contains(searchTerm) ||
	                livre.getAuteur().toLowerCase().contains(searchTerm)) {
	                
	                model.addRow(new Object[]{
	                    livre.getId(),
	                    livre.getTitre(),
	                    livre.getAuteur(),
	                    livre.getExemplairesDisponibles()
	                });
	            }
	        }
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(this, 
	            "Erreur lors de la recherche: " + e.getMessage(),
	            "Erreur",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	}

	private void searchEtudiants(String searchTerm) {
	    DefaultTableModel model = (DefaultTableModel) etudiantsTable.getModel();
	    model.setRowCount(0);
	    
	    try {
	        for (Etudiant etudiant : etudiantDAO.getTousLesEtudiants()) {
	            if (etudiant.getNom().toLowerCase().contains(searchTerm) ||
	                etudiant.getPrenom().toLowerCase().contains(searchTerm) ||
	                etudiant.getNumero().toLowerCase().contains(searchTerm) ||
	                etudiant.getEmail().toLowerCase().contains(searchTerm)) {
	                
	                model.addRow(new Object[]{
	                    etudiant.getId(),
	                    etudiant.getNom(),
	                    etudiant.getPrenom(),
	                    etudiant.getNumero(),
	                    etudiant.getEmail()
	                });
	            }
	        }
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(this, 
	            "Erreur lors de la recherche: " + e.getMessage(),
	            "Erreur",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	}

	private void searchEmprunts(String searchTerm) {
	    DefaultTableModel model = (DefaultTableModel) empruntsTable.getModel();
	    model.setRowCount(0);
	    
	    try {
	        for (Emprunt emprunt : empruntDAO.getTousLesEmprunts()) {
	            if (emprunt.getEtudiant().getNom().toLowerCase().contains(searchTerm) ||
	                emprunt.getLivre().getTitre().toLowerCase().contains(searchTerm)   ) {
	                
	                model.addRow(new Object[]{
	                    emprunt.getId(),
	                    emprunt.getEtudiant().getNom() + " " + emprunt.getEtudiant().getPrenom(),
	                    emprunt.getLivre().getTitre(),
	                    formatDate(emprunt.getDateEmprunt()),
	                    emprunt.getStatut()
	                });
	            }
	        }
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(this, 
	            "Erreur lors de la recherche: " + e.getMessage(),
	            "Erreur",
	            JOptionPane.ERROR_MESSAGE
	        );
	    }
	}
	/////////////////////////////////////////////////////////////////////////////////////////////
	            


	
	

	
	
	
	private JPanel createEmpruntViewPanel() {
	    JPanel empruntViewPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
	    empruntViewPanel.setBackground(Color.WHITE);  // White background for the panel
	    empruntViewPanel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

	    // Create and customize the JCheckBox
	    JCheckBox checkBox = new JCheckBox("Afficher Tout");
	    checkBox.setBackground(Color.WHITE);  // White background for the checkbox
	    checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));  // Set the font for the checkbox

	    checkBox.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e){
	            if (e.getStateChange() == ItemEvent.SELECTED) {
	                AfficherTout = true;  
	                try {
						populateEmpruntsTable();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                //handleCheckboxChecked();
	            } else {
	            	AfficherTout = false;  
	            	try {
						populateEmpruntsTable();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                //handleCheckboxUnchecked();
	            }
	        }
	    });

	    empruntViewPanel.add(checkBox);  // Add the checkbox to the panel
	    return empruntViewPanel;
	}


    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return tablePanel;
    }

    private JPanel createButtonsPanel(String[] buttonTexts, ActionListener[] listeners) {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = createStyledButton(buttonTexts[i], PRIMARY_COLOR);
            button.addActionListener(listeners[i]);
            buttonsPanel.add(button);
        }

        return buttonsPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Store the original background color
        final Color originalColor = backgroundColor;

        // Add mouse listener to change button color when clicked
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Darken the color when clicked
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Reset the color after release
                button.setBackground(originalColor);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Optionally, you can lighten the color slightly when the mouse enters the button
                button.setBackground(originalColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Reset the color when the mouse exits the button
                button.setBackground(originalColor);
            }
        });

        return button;
    }


    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);

        return table;
    }


    private void populateLivresTable() {
        DefaultTableModel model = (DefaultTableModel) livresTable.getModel();
        model.setRowCount(0); // Clear existing rows

        try {
            for (Livre livre : livreDAO.getTousLesLivres()) {
                model.addRow(new Object[]{
                    livre.getId(),
                    livre.getTitre(),
                    livre.getAuteur(),
                    livre.getEditeur(),
                    livre.getDatePublication(),
                    livre.getExemplairesDisponibles(),

                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Erreur lors de la récupération des livres: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateEtudiantsTable() {
        DefaultTableModel model = (DefaultTableModel) etudiantsTable.getModel();
        model.setRowCount(0); // Clear existing rows

        try {
            for (Etudiant etudiant : etudiantDAO.getTousLesEtudiants()) {
                model.addRow(new Object[]{
                    etudiant.getId(),
                    etudiant.getNom(),
                    etudiant.getPrenom(),
                    etudiant.getNumero(),
                    etudiant.getEmail(),
                    etudiant.getFiliere(),
                    etudiant.getAge()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Erreur lors de la récupération des étudiants: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateEmpruntsTable() throws SQLException {
    	// Declare the variable listLivre as a List of Livre objects (or the appropriate type for your use case)
    	List<Emprunt> listLivre;

    	// Initialize listLivre by calling the appropriate method from the DAO
    	listLivre = empruntDAO.getTousLesEmprunts();  // Get all emprunts initially

    	// Check if the 'AfficherTout' flag is false
    	if (!AfficherTout) {
    	    // If not, get only the emprunts that are 'en cours'
    	    listLivre = empruntDAO.getEmpruntEnCours();
    	}

        DefaultTableModel model = (DefaultTableModel) empruntsTable.getModel();
        model.setRowCount(0); // Clear existing rows

        for (Emprunt emprunt : listLivre) {
		    model.addRow(new Object[]{
		        emprunt.getId(),
		        emprunt.getEtudiant().getNom(),
		        emprunt.getLivre().getTitre(),
		        emprunt.getDateEmprunt(),
		        emprunt.getDateRetourPrevue(),
		        emprunt.getDateRetourEffective(),
		        emprunt.getStatut().getDisplayName()
		    });
		}
}
    
//    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        EventQueue.invokeLater(() -> {
//            try {
//                BibliothequeGUI frame = new BibliothequeGUI();
//                frame.setVisible(true);
//                
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
}