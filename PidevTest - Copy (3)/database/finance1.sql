-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Feb 15, 2026 at 05:44 PM
-- Server version: 8.4.7
-- PHP Version: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `finance1`
--

-- --------------------------------------------------------

--
-- Table structure for table `abonnement`
--

DROP TABLE IF EXISTS `abonnement`;
CREATE TABLE IF NOT EXISTS `abonnement` (
  `id_abonnement` int NOT NULL AUTO_INCREMENT,
  `type_abonnement` enum('Bronze','Silver','Gold','Platinum') COLLATE utf8mb4_unicode_ci NOT NULL,
  `prix_mensuel` decimal(10,2) DEFAULT NULL,
  `prix_annuel` decimal(10,2) DEFAULT NULL,
  `duree` enum('mensuel','annuel') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `statut_abonnement` enum('actif','expire','annule') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_renouvellement_auto` tinyint(1) DEFAULT NULL,
  `id_user` int DEFAULT NULL,
  PRIMARY KEY (`id_abonnement`),
  KEY `fk_abonnement_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `assurance`
--

DROP TABLE IF EXISTS `assurance`;
CREATE TABLE IF NOT EXISTS `assurance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int NOT NULL,
  `type_assurance` enum('VIE','SANTE','AUTO','HABITATION','RESPONSABILITE_CIVILE','SCOLAIRE','VOYAGE','PROFESSIONNELLE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `compagnie` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_police` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `montant_couverture` decimal(15,2) DEFAULT NULL,
  `franchise` decimal(15,2) DEFAULT NULL,
  `prime_annuelle` decimal(15,2) DEFAULT NULL,
  `prime_mensuelle` decimal(15,2) DEFAULT NULL,
  `date_debut` date DEFAULT NULL,
  `date_echeance` date DEFAULT NULL,
  `mode_paiement` enum('MENSUEL','TRIMESTRIEL','SEMESTRIEL','ANNUEL') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `statut` enum('ACTIF','EXPIRE','RESILIE','SUSPENDU') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIF',
  `renouvellement_auto` tinyint(1) DEFAULT NULL,
  `garanties_incluses` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_police` (`numero_police`),
  KEY `fk_assurance_user` (`utilisateur_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `compte_bancaire`
--

DROP TABLE IF EXISTS `compte_bancaire`;
CREATE TABLE IF NOT EXISTS `compte_bancaire` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `numero_compte` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `titulaire` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `solde` decimal(15,2) DEFAULT '0.00',
  `devise` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type_compte` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_creation` date NOT NULL,
  `actif` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_compte` (`numero_compte`),
  KEY `fk_compte_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `contrat_assurance`
--

DROP TABLE IF EXISTS `contrat_assurance`;
CREATE TABLE IF NOT EXISTS `contrat_assurance` (
  `id` int NOT NULL AUTO_INCREMENT,
  `assurance_id` int NOT NULL,
  `numero_contrat` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_signature` date DEFAULT NULL,
  `date_fin_contrat` date DEFAULT NULL,
  `duree_contrat` int DEFAULT NULL,
  `conditions_particulieres` text COLLATE utf8mb4_unicode_ci,
  `exclusions` text COLLATE utf8mb4_unicode_ci,
  `plafond_annuel` decimal(15,2) DEFAULT NULL,
  `taux_remboursement` decimal(5,2) DEFAULT NULL,
  `delai_carence` int DEFAULT NULL,
  `clause_beneficiaire` text COLLATE utf8mb4_unicode_ci,
  `document_contrat` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amendements` json DEFAULT NULL,
  `conseiller_attribue` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacts` text COLLATE utf8mb4_unicode_ci,
  `statut` enum('ACTIF','EXPIRE','RESILIE','EN_ATTENTE') COLLATE utf8mb4_unicode_ci DEFAULT 'EN_ATTENTE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_contrat` (`numero_contrat`),
  KEY `fk_contrat_assurance` (`assurance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `depense`
--

DROP TABLE IF EXISTS `depense`;
CREATE TABLE IF NOT EXISTS `depense` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `montant` decimal(15,2) NOT NULL,
  `date_depense` date NOT NULL,
  `categorie` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mode_paiement` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `compte_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_depense_compte` (`compte_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `investissement`
--

DROP TABLE IF EXISTS `investissement`;
CREATE TABLE IF NOT EXISTS `investissement` (
  `idinves` int NOT NULL AUTO_INCREMENT,
  `montantinvesti` int DEFAULT NULL,
  `dateinves` date DEFAULT NULL,
  `modepaiement` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `statut_investissement` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `idprojet` int NOT NULL,
  PRIMARY KEY (`idinves`),
  KEY `fk_investissement_projet` (`idprojet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `projet`
--

DROP TABLE IF EXISTS `projet`;
CREATE TABLE IF NOT EXISTS `projet` (
  `idprojet` int NOT NULL AUTO_INCREMENT,
  `nomprojet` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `secteur` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `montant_objectif` int DEFAULT NULL,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `statut_projet` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`idprojet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prenom` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mot_de_passe` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_naissance` date DEFAULT NULL,
  `role` enum('admin','client','organisateur') COLLATE utf8mb4_unicode_ci NOT NULL,
  `statut_compte` enum('actif','desactive') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_projet`
--

DROP TABLE IF EXISTS `user_projet`;
CREATE TABLE IF NOT EXISTS `user_projet` (
  `user_id` int NOT NULL,
  `projet_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`projet_id`),
  KEY `fk_userprojet_projet` (`projet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `abonnement`
--
ALTER TABLE `abonnement`
  ADD CONSTRAINT `fk_abonnement_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `assurance`
--
ALTER TABLE `assurance`
  ADD CONSTRAINT `fk_assurance_user` FOREIGN KEY (`utilisateur_id`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `compte_bancaire`
--
ALTER TABLE `compte_bancaire`
  ADD CONSTRAINT `fk_compte_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `contrat_assurance`
--
ALTER TABLE `contrat_assurance`
  ADD CONSTRAINT `fk_contrat_assurance` FOREIGN KEY (`assurance_id`) REFERENCES `assurance` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `depense`
--
ALTER TABLE `depense`
  ADD CONSTRAINT `fk_depense_compte` FOREIGN KEY (`compte_id`) REFERENCES `compte_bancaire` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `investissement`
--
ALTER TABLE `investissement`
  ADD CONSTRAINT `fk_investissement_projet` FOREIGN KEY (`idprojet`) REFERENCES `projet` (`idprojet`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `user_projet`
--
ALTER TABLE `user_projet`
  ADD CONSTRAINT `fk_userprojet_projet` FOREIGN KEY (`projet_id`) REFERENCES `projet` (`idprojet`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_userprojet_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
