-- Credit and Remboursement Tables Schema
-- Integrated with existing finance1 database

USE finance1;

-- ============================================
-- CREDIT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS `credit` (
  `id_credit` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `compte_id` BIGINT(20) NOT NULL,
  `montant_demande` DECIMAL(15,2) NOT NULL,
  `type_credit` ENUM('3M','6M','12M','24M','36M') NOT NULL COMMENT 'Duration in months',
  `taux_interet` DECIMAL(5,2) NOT NULL COMMENT 'Interest rate percentage',
  `montant_total` DECIMAL(15,2) NOT NULL COMMENT 'Total amount to pay (principal + interest)',
  `montant_restant` DECIMAL(15,2) NOT NULL COMMENT 'Remaining amount to pay',
  `date_demande` DATE NOT NULL,
  `date_debut` DATE NULL,
  `date_fin` DATE NULL,
  `statut_credit` ENUM('EN_ATTENTE','ACCEPTE','REFUSE','EN_COURS','TERMINE','EN_RETARD') NOT NULL DEFAULT 'EN_ATTENTE',
  `motif_refus` VARCHAR(255) NULL,
  `mensualite` DECIMAL(15,2) NULL COMMENT 'Monthly payment amount',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_credit`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_compte_id` (`compte_id`),
  KEY `idx_statut` (`statut_credit`),
  CONSTRAINT `fk_credit_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_credit_compte` FOREIGN KEY (`compte_id`) REFERENCES `compte_bancaire` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- REMBOURSEMENT TABLE
-- Handles both credit repayments and refunds (from cancellations)
-- ============================================
CREATE TABLE IF NOT EXISTS `remboursement` (
  `id_remboursement` INT(11) NOT NULL AUTO_INCREMENT,
  `credit_id` INT(11) NULL COMMENT 'For credit repayments, NULL for refunds',
  `user_id` INT(11) NOT NULL,
  `compte_id` BIGINT(20) NOT NULL,
  `type_remboursement` ENUM('CREDIT_PAYMENT','ABONNEMENT_REFUND','ASSURANCE_REFUND') NOT NULL,
  `montant` DECIMAL(15,2) NOT NULL,
  `date_remboursement` DATE NOT NULL,
  `statut` ENUM('EN_ATTENTE','COMPLETE','ECHOUE') NOT NULL DEFAULT 'EN_ATTENTE',
  `description` TEXT NULL,
  `reference_id` INT(11) NULL COMMENT 'Reference to cancelled subscription/insurance',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_remboursement`),
  KEY `idx_credit_id` (`credit_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_compte_id` (`compte_id`),
  KEY `idx_type` (`type_remboursement`),
  CONSTRAINT `fk_remboursement_credit` FOREIGN KEY (`credit_id`) REFERENCES `credit` (`id_credit`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_remboursement_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_remboursement_compte` FOREIGN KEY (`compte_id`) REFERENCES `compte_bancaire` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================
-- INTEREST RATE CONFIGURATION BY SUBSCRIPTION TYPE
-- ============================================
-- Base interest rates (can be adjusted):
-- No subscription: 15% interest
-- Basic: 12% interest
-- Premium: 10% interest
-- Gold: 8% interest

-- Sample data for testing
INSERT INTO `credit` (`user_id`, `compte_id`, `montant_demande`, `type_credit`, `taux_interet`, `montant_total`, `montant_restant`, `date_demande`, `statut_credit`) VALUES
(1, 1, 5000.00, '12M', 12.00, 5600.00, 5600.00, '2026-02-01', 'EN_ATTENTE');

-- Notes:
-- 1. When user requests credit, check if they have active subscription
-- 2. Calculate interest based on subscription type
-- 3. Credit is deposited to compte_bancaire when ACCEPTE
-- 4. Monthly remboursements are created and processed
-- 5. When subscription/insurance is cancelled, create refund remboursement
