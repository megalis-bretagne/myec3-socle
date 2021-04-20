-- SCRIPT SQL pour une entreprise complet id 2
-- REQUIRED initData.sql

-- Organism 1
INSERT INTO Structure
(id, externalId, label, name, acronym, canton, city, country, postalAddress, postalCode, description, email, enabled, fax, iconUrl, logoUrl, phone, siren, website, workforce, structureType_id,  tenantIdentifier, additionalInfoAddress, insee, localityAddress)
VALUES (2, 2, 'Company test u', 'Company test u', 'a0y', NULL, 'Rennes', 'FR', 'Place république', '35000', NULL, 'structure-2@mail.fr',0, NULL, 'https://combrit-socle.sib.fr/externe/logos/icon_structure_id_31952.jpeg', 'https://combrit-socle.sib.fr/externe/logos/logo_structure_id_31952.jpeg', NULL, '150248938', 'https://www.megalisbretagne.org', NULL, 1, '2.TU', NULL, NULL, NULL);

INSERT INTO Company
    (id, RCS, RM, apeCode, legalCategory, registrationCountry )
VALUES
    (2, 0,0,'2013B', 'SARL','FR');

INSERT INTO Department (id, label, name, acronym, postalAddress, postalCode, email, city)
VALUES
       (2, 'RACINE MEGALIS TEST', 'ROOT MEGALIS', 'TU', 'ZAC des Champs Blancs 15 rue Claude Chappe  Bâtiment B', '3500', 'email@mail.fr', 'rennes');

-- DEPARTMENT Company
INSERT INTO CompanyDepartment (id, company_id)
    VALUES (2, 2 );

-- ETABLISSEMENTS
INSERT INTO Establishment
    (id, company_id, name, label, city,postalCode, postalAddress, apeCode, email, isHeadOffice, diffusableInformations)
VALUES
    (1, 2, 'MYEC3 test','MYEC3 test', 'Rennes','35000','2 rue de Chateaugiron','5630Z', 'myec3test@mail.fr', 0,0),
    (2, 2, 'ATEXO','ATEXO', 'Rennes','35000','2 rue de Chateaugiron','5630Z', 'atexo@mail.fr', 0,0),
    (3, 2, 'SIB','SIB', 'Rennes','35000','2 rue de Chateaugiron','5630Z', 'sib@mail.fr', 0,0);



-- EMPLOYEE

INSERT INTO User (id, name, firstname, lastname,enabled, username, connectionAttempts)
VALUES
    (2000, 'USER 2000 Est 1', 'Lapin', 'Simon',  1, 'USER-2000@mail.fr',0),
    (2001, 'USER 2001 Est 1', 'Tchoupi', 'Tchoupi', 1, 'USER-2001@mail.fr',0),
    (2002, 'USER 2002 Est 2', 'SIB', 'SIB', 1, 'USER-2002@mail.fr',0),
    (2003, 'USER 2003 Est 3', 'Ane', 'Trotro', 1, 'USER-2003@mail.fr',0);


INSERT INTO Profile (id,user_id, label, name, city, enabled,email, postalAddress, postalCode)
VALUES
    (2000,2000, 'Profil 2000 Est 1', 'Agent Lapin Simon', 'Rennes', 1, 'lapin@mail.fr', 'rennes', '3500'),
    (2001,2001, 'Profil 2001 Est 1', 'Agent Tchoupi', 'Rennes', 1, 'tchoupi@mail.fr', 'rennes', '3500'),
    (2002,2002, 'Profil 2002 Est 2', 'Agent SIB', 'Rennes', 1, 'sib@mail.fr', 'rennes', '3500'),
    (2003,2003, 'Profil 2003 Est 3', 'Agent TroTro', 'Rennes', 1, 'trotro@mail.fr','rennes', '3500');

INSERT INTO EmployeeProfile (id,companyDepartment_id, establishment_id )
     VALUES
            (2000, 2, 1 ),
            (2001,2,1),
            (2002,2,2),
            (2003,2,3);




