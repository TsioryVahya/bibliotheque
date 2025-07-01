CREATE TABLE Profils_Emprunteur (
    id_profil SERIAL PRIMARY KEY,
    nom_profil VARCHAR(100) NOT NULL UNIQUE,
    quota_emprunts_simultanes INT NOT NULL DEFAULT 3,
    duree_pret_domicile_jours INT NOT NULL DEFAULT 21, -- Durée standard pour un prêt à domicile
    duree_pret_sur_place_heures INT NOT NULL DEFAULT 3, -- Durée pour une consultation sur place
    peut_prolonger_pret BOOLEAN NOT NULL DEFAULT TRUE,
    montant_penalite_par_jour DECIMAL(5, 2) NOT NULL DEFAULT 0.50
);

-- Types d'abonnements (Annuel, Mensuel...)
CREATE TABLE Types_Abonnement (
    id_type_abonnement SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    duree_en_jours INT NOT NULL,
    tarif DECIMAL(10, 2) NOT NULL
);

-- Auteurs, Éditeurs, Catégories
CREATE TABLE Auteurs (
    id_auteur SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100)
);

CREATE TABLE Editeurs (
    id_editeur SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE Categories (
    id_categorie SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- Table pour les jours fériés et weekends (pour le calcul des dates de retour)
CREATE TABLE Jours_Feries (
    date_ferie DATE PRIMARY KEY,
    description VARCHAR(255)
);

-- =================================================================
-- 2. ENTITÉS PRINCIPALES
-- =================================================================

-- La table des livres (l'œuvre abstraite)
CREATE TABLE Livres (
    id_livre SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    annee_publication INT,
    resume TEXT,
    id_editeur INT,
    FOREIGN KEY (id_editeur) REFERENCES Editeurs(id_editeur)
);

-- La table des exemplaires (la copie physique)
-- 1. D'abord créer la table des états
CREATE TABLE Etats_Exemplaire (
    id_etat SERIAL PRIMARY KEY,
    code_etat VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(50) NOT NULL,
    peut_etre_emprunte BOOLEAN NOT NULL DEFAULT FALSE
);

-- 2. Insérer les états prédéfinis
INSERT INTO Etats_Exemplaire (code_etat, libelle, peut_etre_emprunte) VALUES
('DISPONIBLE', 'Disponible', true),
('EMPRUNTE', 'Emprunté', false),
('RESERVE', 'Réservé', false),
('REPARATION', 'En réparation', false),
('PERDU', 'Perdu', false);

-- 3. Créer la table Exemplaires avec référence à Etats_Exemplaire
CREATE TABLE Exemplaires (
    id_exemplaire SERIAL PRIMARY KEY,
    id_livre INT NOT NULL,
    code_barres VARCHAR(100) UNIQUE NOT NULL,
    id_etat INT NOT NULL DEFAULT 1, -- 1 = DISPONIBLE par défaut
    consultable_sur_place_uniquement BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_livre) REFERENCES Livres(id_livre) ON DELETE CASCADE,
    FOREIGN KEY (id_etat) REFERENCES Etats_Exemplaire(id_etat)
);

-- La table des emprunteurs (utilisateurs)
CREATE TABLE Emprunteurs (
    id_emprunteur SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    date_inscription DATE NOT NULL DEFAULT CURRENT_DATE,
    id_profil INT NOT NULL,
    FOREIGN KEY (id_profil) REFERENCES Profils_Emprunteur(id_profil)
);

-- =================================================================
-- 3. TABLES DE LIAISON ET TRANSACTIONS
-- =================================================================

-- Liaison Plusieurs-à-Plusieurs entre Livres et Auteurs
CREATE TABLE Livres_Auteurs (
    id_livre INT NOT NULL,
    id_auteur INT NOT NULL,
    PRIMARY KEY (id_livre, id_auteur),
    FOREIGN KEY (id_livre) REFERENCES Livres(id_livre) ON DELETE CASCADE,
    FOREIGN KEY (id_auteur) REFERENCES Auteurs(id_auteur) ON DELETE CASCADE
);

-- Liaison Plusieurs-à-Plusieurs entre Livres et Catégories
CREATE TABLE Livres_Categories (
    id_livre INT NOT NULL,
    id_categorie INT NOT NULL,
    PRIMARY KEY (id_livre, id_categorie),
    FOREIGN KEY (id_livre) REFERENCES Livres(id_livre) ON DELETE CASCADE,
    FOREIGN KEY (id_categorie) REFERENCES Categories(id_categorie) ON DELETE CASCADE
);

-- Gère les abonnements actifs des emprunteurs
CREATE TABLE Abonnements (
    id_abonnement SERIAL PRIMARY KEY,
    id_emprunteur INT NOT NULL,
    id_type_abonnement INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut ENUM('actif', 'expire', 'annule') NOT NULL DEFAULT 'actif',
    FOREIGN KEY (id_emprunteur) REFERENCES Emprunteurs(id_emprunteur),
    FOREIGN KEY (id_type_abonnement) REFERENCES Types_Abonnement(id_type_abonnement)
);

-- Table pour gérer les droits d'emprunt spécifiques (un livre interdit à un profil)
CREATE TABLE Droits_Emprunt_Specifiques (
    id_droit SERIAL PRIMARY KEY,
    id_livre INT NOT NULL,
    id_profil INT NOT NULL,
    emprunt_domicile_autorise BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (id_livre, id_profil), -- Un seul droit par couple livre/profil
    FOREIGN KEY (id_livre) REFERENCES Livres(id_livre) ON DELETE CASCADE,
    FOREIGN KEY (id_profil) REFERENCES Profils_Emprunteur(id_profil) ON DELETE CASCADE
);


-- Table des transactions d'emprunt
CREATE TABLE Emprunts (
    id_emprunt SERIAL PRIMARY KEY,
    id_exemplaire INT NOT NULL,
    id_emprunteur INT NOT NULL,
    date_emprunt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_retour_prevue DATE NOT NULL,
    date_retour_reelle DATE, -- NULL tant que l'exemplaire n'est pas retourné
    type_emprunt ENUM('a_domicile', 'sur_place') NOT NULL,
    prolongations INT DEFAULT 0, -- Compteur de prolongations
    FOREIGN KEY (id_exemplaire) REFERENCES Exemplaires(id_exemplaire),
    FOREIGN KEY (id_emprunteur) REFERENCES Emprunteurs(id_emprunteur)
);

-- 1. Création de la table des statuts de réservation
CREATE TABLE Statuts_Reservation (
    id_statut SERIAL PRIMARY KEY,
    code_statut VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(50) NOT NULL,
    est_actif BOOLEAN NOT NULL DEFAULT FALSE
);

-- 2. Insertion des statuts prédéfinis
INSERT INTO Statuts_Reservation (code_statut, libelle, est_actif) VALUES
('ACTIVE', 'Active', true),
('HONOREE', 'Honorée', false),
('ANNULEE', 'Annulée', false),
('EXPIREE', 'Expirée', false);

-- 3. Création de la table Reservations avec référence à Statuts_Reservation
CREATE TABLE Reservations (
    id_reservation SERIAL PRIMARY KEY,
    id_livre INT NOT NULL,
    id_emprunteur INT NOT NULL,
    id_statut INT NOT NULL DEFAULT 1, -- 1 = ACTIVE par défaut
    date_demande TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration DATE NOT NULL,
    FOREIGN KEY (id_livre) REFERENCES Livres(id_livre) ON DELETE CASCADE,
    FOREIGN KEY (id_emprunteur) REFERENCES Emprunteurs(id_emprunteur) ON DELETE CASCADE,
    FOREIGN KEY (id_statut) REFERENCES Statuts_Reservation(id_statut)
);

-- Table des pénalités
CREATE TABLE Penalites (
    id_penalite SERIAL PRIMARY KEY,
    id_emprunt INT NOT NULL, -- La pénalité est liée à un emprunt spécifique
    id_emprunteur INT NOT NULL,
    montant_calcule DECIMAL(10, 2) NOT NULL,
    date_creation DATE NOT NULL DEFAULT CURRENT_DATE,
    statut ENUM('active', 'payee', 'annulee') NOT NULL DEFAULT 'active',
    date_paiement DATE,
    FOREIGN KEY (id_emprunt) REFERENCES Emprunts(id_emprunt),
    FOREIGN KEY (id_emprunteur) REFERENCES Emprunteurs(id_emprunteur)
);