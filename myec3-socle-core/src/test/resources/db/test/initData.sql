-- APPLICATION
INSERT INTO Application
    (id, label, name, url )
VALUES
    (1, 'Gestion mon comptes', 'GU', 'https://test-sib'),
    (2, 'Télétransmission ACTES et/ou HELIOS', 'Pastell', 'https://test-sib'),
    (3,  'Salle des marchés', 'SDM', 'https://test-sib'),
    (4,  'Parapheur', 'Application - I-Parapheur', 'https://test-sib'),
    (6,  'Portail Mégalis Bretagne', 'portail megalisbretagne', 'https://test-sib'),
    (7,  'Parapheur Mutualisé', 'Application - Parapheur - Mono', 'https://test-sib'),
    (8,  'Test modification generation tenant', 'APPLI_JTH', 'https://test-sib'),
    (9,  'Publication open data', 'pub-open-data', 'https://test-sib');


-- CUSTOMER 1
INSERT INTO Customer VALUES (1, 1, 'Mégalis Bretagne', 'MB', NULL, 1, NULL, NULL, NULL, NULL, 'https://www.megalisbretagne.org/jcms/pmw_9305/megalis-mon-compte-colonne-mes-services ');

INSERT INTO Customer_Application VALUES (1,1);

-- StructureType
INSERT INTO StructureType VALUES (2, 'COMPANY');
INSERT INTO StructureType VALUES (1, 'ORGANISM');


INSERT INTO SviProfile VALUES (1);

