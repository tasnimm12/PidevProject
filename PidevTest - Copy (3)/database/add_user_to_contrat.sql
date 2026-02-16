-- Migration: Add utilisateur_id to contrat_assurance table
-- This links each contract to the user who subscribed to it

ALTER TABLE `contrat_assurance` 
ADD COLUMN `utilisateur_id` INT NOT NULL AFTER `assurance_id`,
ADD KEY `fk_contrat_user` (`utilisateur_id`),
ADD CONSTRAINT `fk_contrat_user` FOREIGN KEY (`utilisateur_id`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;
