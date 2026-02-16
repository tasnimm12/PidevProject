-- Create Admin User for Testing Credix Application
-- Run this after importing the main schema

USE finance1;

-- Create Admin Account
INSERT INTO users (nom, prenom, email, mot_de_passe, telephone, date_naissance, role, statut_compte)
VALUES ('Admin', 'Credix', 'admin@credix.com', 'admin123', '+1234567890', '1990-01-01', 'admin', 'actif');

-- Create Client Account
INSERT INTO users (nom, prenom, email, mot_de_passe, telephone, date_naissance, role, statut_compte)
VALUES ('Doe', 'John', 'client@credix.com', 'client123', '+1234567891', '1995-05-15', 'client', 'actif');

-- Create Organisateur Account
INSERT INTO users (nom, prenom, email, mot_de_passe, telephone, date_naissance, role, statut_compte)
VALUES ('Smith', 'Jane', 'organizer@credix.com', 'org123', '+1234567892', '1992-08-20', 'organisateur', 'actif');

COMMIT;

SELECT 'Test accounts created successfully!' as Message;
SELECT * FROM users;
