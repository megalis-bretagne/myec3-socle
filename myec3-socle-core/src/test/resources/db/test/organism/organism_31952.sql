-- SCRIPT SQL pour un organism complet
-- REQUIRED initData.sql

-- Organism 31592
INSERT INTO Structure
(id, externalId, label, name, acronym, canton, city, country, postalAddress, postalCode, description, email, enabled, fax, iconUrl, logoUrl, phone, siren, website, workforce, structureType_id,  tenantIdentifier, additionalInfoAddress, insee, localityAddress)
    VALUES (31952, 31952, 'zTest_Megalisd', 'zTest_Megalis', 'a0y', NULL, 'Cesson Cevignee', 'FR', 'ZAC des Champs Blancs 15 rue Claude Chappe  Bâtiment B', '35510', NULL, 'myec3.sib+31952-CessonCevi@gmail.com',0, NULL, 'https://combrit-socle.sib.fr/externe/logos/icon_structure_id_31952.jpeg', 'https://combrit-socle.sib.fr/externe/logos/logo_structure_id_31952.jpeg', NULL, '150248938', 'https://www.megalisbretagne.org', NULL, 1, '31952.megalis', NULL, NULL, NULL);

INSERT INTO Organism
(id,customer_id , budget, college, contributionAmount, legalCategory, membership )
    VALUES (31952, 1,  NULL, NULL, NULL, '_1', 1);

INSERT INTO Department (id, label, name, acronym, postalAddress, postalCode, email, city)
VALUES
    (32816, 'RACINE MEGALIS TEST', 'ROOT MEGALIS', 'TU', 'ZAC des Champs Blancs 15 rue Claude Chappe  Bâtiment B', '3500', 'email@mail.fr', 'rennes'),
    (35617, 'Niveau Sous Sol', 'sous sol MEGALIS', 'TUS', 'ZAC des Champs Blancs 15 rue Claude Chappe  Bâtiment B', '3500', 'megalis-sous-sol@mail.fr', 'rennes'),
    (47549, 'Niveau Top ROOF', 'LE TOP DU TOP', 'TOP', 'ZAC des Champs Blancs 15 rue Claude Chappe  Bâtiment B (mais sur le toit)', '3500', 'on-roof-top@mail.fr', 'rennes');



-- DEPARTMENT ORGANISM
INSERT INTO OrganismDepartment (id, abbreviation, organism_id, parentDepartment_id)
    VALUES (32816, null, 31952, NULL ), (35617, 'CHILD1', 31952, 32816 ), (47549, 'CHILD2', 31952, 32816 );


INSERT INTO User (id, name, firstname, lastname,enabled, username, connectionAttempts)
VALUES
(1, 'USER 1 OrgDepart 32816', 'Lapin', 'Simon',  1, 'lapin-login@mail.fr',0),
(2, 'USER 2 OrgDepart 32816', 'Tchoupi', 'Tchoupi', 1, 'tchoupi-login@mail.fr',0),
(3, 'USER 3 OrgDepart 35617', 'SIB', 'SIB', 1, 'sib-login@mail.fr',0),
(4, 'USER 4 OrgDepart 47549', 'Ane', 'Trotro', 1, 'trotro-login@mail.fr',0);


INSERT INTO Profile (id,user_id, label, name, city, enabled,email, postalAddress, postalCode)
VALUES
    (1,1, 'Profil 1 OrgDepart 32816', 'Agent Lapin Simon', 'Rennes', 1, 'lapin@mail.fr', 'rennes', '3500'),
    (2,2, 'Profil 2 OrgDepart 32816', 'Agent Tchoupi', 'Rennes', 1, 'tchoupi@mail.fr', 'rennes', '3500'),
    (3,3, 'Profil 3 OrgDepart 35617', 'Agent SIB', 'Rennes', 1, 'sib@mail.fr', 'rennes', '3500'),
    (4,4, 'Profil 4 OrgDepart 47549', 'Agent TroTro', 'Rennes', 1, 'trotro@mail.fr','rennes', '3500');

-- AGENT
INSERT INTO AgentProfile (id, organismDepartment_id, elected, representative, executive, substitute)
    VALUES
           (1, 32816,0 ,0 ,0,0),
           (2, 32816,0 ,0 ,0,0),
           (3, 35617,0 ,0 ,0,0),
           (4, 47549, 0 ,0 ,0,0);




