-- SCRIPT SQL pour un organism complet id 1
-- REQUIRED initData.sql

-- Organism 1
INSERT INTO Structure
(id, externalId, label, name, acronym, canton, city, country, postalAddress, postalCode, description, email, enabled, fax, iconUrl, logoUrl, phone, siren, website, workforce, structureType_id,  tenantIdentifier, additionalInfoAddress, insee, localityAddress)
    VALUES (1, 1, 'TEST U', 'TEST U', 'a0y', NULL, 'Cesson Cevignee', 'FR', 'ZAC des Champs Blancs 15 rue Claude Chappe  Bâtiment B', '35510', NULL, 'myec3.sib+31952-CessonCevi@gmail.com',0, NULL, 'https://combrit-socle.sib.fr/externe/logos/icon_structure_id_31952.jpeg', 'https://combrit-socle.sib.fr/externe/logos/logo_structure_id_31952.jpeg', NULL, '150248938', 'https://www.megalisbretagne.org', NULL, 1, '1.TU', NULL, NULL, NULL);

INSERT INTO Organism
(id,customer_id , budget, college, contributionAmount, legalCategory, membership )
    VALUES (1, 1,  NULL, NULL, NULL, '_1', 1);

INSERT INTO Structure_Application (structures_id, applications_id)
 VALUES (1, 1);

INSERT INTO Department (id, label, name, acronym, postalAddress, postalCode, email, city)
VALUES
    (1, 'RACINE TU TEST', 'ROOT TU', 'TU', 'ZAC des Champs Blancs 15 rue Claude Chappe  Bâtiment B', '3500', 'tu@mail.fr', 'rennes');

-- DEPARTMENT ORGANISM
INSERT INTO OrganismDepartment (id, abbreviation, organism_id, parentDepartment_id)
    VALUES (1, null, 1, NULL );


INSERT INTO User (id, name, firstname, lastname,enabled, username, connectionAttempts)
VALUES
(1000, 'USER 1000 OrgDepart 32816', 'Agent', '1000',  1, '1000-login@mail.fr',0);

INSERT INTO Profile (id, user_id, label, name, city, enabled,email, postalAddress, postalCode)
VALUES
    (1000,1000, 'Profil 1000 OrgDepart 1', 'Agent 1000', 'Rennes', 1, 'lapin@mail.fr', 'rennes', '3500');

-- AGENT
INSERT INTO AgentProfile (id, organismDepartment_id, elected, representative, executive, substitute)
    VALUES
           (1000, 1,0 ,0 ,0,0);



