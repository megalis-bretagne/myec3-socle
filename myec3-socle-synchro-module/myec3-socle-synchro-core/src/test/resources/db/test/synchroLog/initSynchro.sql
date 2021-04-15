
-- Subscription
INSERT INTO SynchronizationSubscription
    (id, resourceLabel, uri, application_id, synchronizationFilter_id)
VALUES
    (10,'ORGANISM', 'https://pastell-preprod.megalis.bretagne.bzh/provisionning-myec3/organism/', 2, 1),
    (11,'ORGANISM_DEPARTMENT', 'https://department', 7, 1),
    (13,'AGENT_PROFILE', 'https://agent', 6, 1),
    (3, 'ORGANISM', 'http://sdm', 3,1);

-- Synchro Log Organisme 31952
INSERT INTO SynchronizationLog
  (resourceId, resourceType, synchronizationSubscription_id, applicationName, synchronizationDate, errorCodeType, errorLabel, errorMessage, httpCode, httpStatus, isFinal, methodType, nbAttempts, responseMessageResourceId, sendingApplication, statut, synchronizationType )
VALUES
    -- TYPE ORGANISM
    (31952, 'ORGANISM',10, 'Pastell', '2021-02-05 12:29:33', NULL, NULL, NULL, 404, 'NOT_FOUND', 0, 'PUT', 0, NULL, 'Socle GU', 'ERROR', 'SYNCHRONIZATION'),
    (31952, 'ORGANISM',3,  'SDM', '2021-02-04 12:00:33', NULL, NULL, NULL, 200, 'OK', 0, 'POST', 0, NULL, 'Socle GU', 'SUCCESS', 'SYNCHRONIZATION'),
    -- TYPE DEPARTEMENT
    (32816, 'ORGANISM_DEPARTMENT',11,  'Parapheur', '2021-01-04 12:00:33', NULL, NULL, NULL, 500, 'INTERNAL_SERVER_ERROR', 0, 'PUT', 0, NULL, 'Socle GU', 'ERROR', 'SYNCHRONIZATION'),
    (32816, 'ORGANISM_DEPARTMENT',11,  'Parapheur', '2021-01-04 12:05:33', NULL, NULL, NULL, 500, 'INTERNAL_SERVER_ERROR', 0, 'PUT', 1, NULL, 'Socle GU', 'ERROR', 'SYNCHRONIZATION'),
    (32816, 'ORGANISM_DEPARTMENT',11,  'Parapheur', '2021-01-04 12:10:33', NULL, NULL, NULL, 200, 'OK', 1, 'PUT', 2, NULL, 'Socle GU', 'SUCCESS', 'SYNCHRONIZATION'),
    (35617, 'ORGANISM_DEPARTMENT',11,  'Parapheur', '2021-01-04 12:10:33', NULL, NULL, NULL, 200, 'OK', 1, 'PUT', 2, NULL, 'Socle GU', 'SUCCESS', 'SYNCHRONIZATION'),

    -- TYPE AGENT
    (1, 'AGENT_PROFILE',13,  'Portail megalis', '2021-01-03 12:10:33', NULL, NULL, NULL, 200, 'OK', 1, 'PUT', 2, NULL, 'Socle GU', 'SUCCESS', 'SYNCHRONIZATION'),
    (4, 'AGENT_PROFILE',13,  'Portail megalis', '2021-01-03 12:10:33', NULL, NULL, NULL, 200, 'OK', 1, 'PUT', 2, NULL, 'Socle GU', 'SUCCESS', 'SYNCHRONIZATION');







