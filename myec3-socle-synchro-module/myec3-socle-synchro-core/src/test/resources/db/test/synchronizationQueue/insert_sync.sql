-- SynchronizationQueue
INSERT INTO SynchronizationQueue (id, resourceId, resourceType, sendingApplication, synchronizationJobType)
VALUES (10,100,'AGENT_PROFILE', 'GU','CREATE');

-- Subscription
INSERT INTO SynchronizationSubscription
(id, resourceLabel, uri, application_id, synchronizationFilter_id)
VALUES
(10,'ORGANISM', 'https://pastell-preprod.megalis.bretagne.bzh/provisionning-myec3/organism/', 2, 1),
(11,'ORGANISM_DEPARTMENT', 'https://department', 7, 1),
(13,'AGENT_PROFILE', 'https://agent', 6, 1),
(3, 'ORGANISM', 'http://sdm', 3,1);
