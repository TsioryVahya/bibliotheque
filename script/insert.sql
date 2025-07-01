-- Insertion de profils d'adhérent exemples
INSERT INTO Profils_Adherent (nom_profil, quota_emprunts_simultanes, duree_pret_domicile_jours, duree_pret_sur_place_heures, peut_prolonger_pret, jours_penalite_par_retard) VALUES
  ('Adulte', 5, 28, 4, TRUE, 2),
  ('Étudiant', 3, 21, 3, TRUE, 1),
  ('Enfant', 2, 14, 2, FALSE, 1),
  ('Senior', 4, 30, 4, TRUE, 1);
