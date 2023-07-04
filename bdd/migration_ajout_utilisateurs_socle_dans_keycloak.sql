-- exécuter avec la commande
-- mysql -u USER -p -D keycloak < migration_ajout_utilisateurs_socle_dans_keycloak.sql
begin;

-- insertion des utilisateurs
insert into USER_ENTITY (ID, EMAIL, EMAIL_CONSTRAINT, EMAIL_VERIFIED, ENABLED, FIRST_NAME, LAST_NAME, REALM_ID,
                         USERNAME, CREATED_TIMESTAMP, NOT_BEFORE)
select UUID()                  as ID,
       p.email                 as EMAIL,
       UUID()                  as EMAIL_CONSTRAINT,
       0                       as EMAIL_VERIFIED,
       p.enabled               as ENABLED,
       u.firstname             as FIRST_NAME,
       u.lastname              as LAST_NAME,
       r.id                    as REALM_ID,
       u.username              as USERNAME,
       UNIX_TIMESTAMP() * 1000 AS CREATED_TIMESTAMP,
       0                       as NOT_BEFORE
from em_socle.Profile p
         join em_socle.`User` u on p.user_id = u.id,
     REALM r
where r.NAME = 'megalis';

-- insertion des roles par défaut pour le realm
insert into USER_ROLE_MAPPING (ROLE_ID, USER_ID)
select kr.ID, ku.id
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join KEYCLOAK_ROLE kr on kr.REALM_ID = r.ID
where r.NAME = 'megalis'
  and kr.NAME = 'default-roles-megalis';

-- insertion des attributs utilisateur
insert into USER_ATTRIBUTE (NAME, VALUE, USER_ID, ID)
select 'externalId' as NAME, p.externalId as VALUE, ku.ID as USER_ID, UUID() as ID
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join em_socle.Profile p on p.user_id = u.id
where r.NAME = 'megalis';

insert into USER_ATTRIBUTE (NAME, VALUE, USER_ID, ID)
select 'uid' as NAME, p.id as VALUE, ku.ID as USER_ID, UUID() as ID
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join em_socle.Profile p on p.user_id = u.id
where r.NAME = 'megalis';

insert into USER_ATTRIBUTE (NAME, VALUE, USER_ID, ID)
select 'userType' as NAME, pt.value as VALUE, ku.ID as USER_ID, UUID() as ID
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join em_socle.Profile p on p.user_id = u.id
         join em_socle.ProfileType pt on pt.id = p.profileType_id
where r.NAME = 'megalis';

-- attention : le select peut retourner des lignes avec VALUE = NULL
-- on ajoute une clause having (filtrage survenant après le select) pour nous protéger de ce cas.
insert into USER_ATTRIBUTE (NAME, VALUE, USER_ID, ID)
select 'alfUserNameMonoTenant' as NAME,
       COALESCE((select CONCAT(p.id, '@monotenant.megalis')
                 from em_socle.Profile_Role pr
                          join em_socle.Role ro on pr.roles_id = ro.id
                 where pr.profiles_id = p.id
                   and ro.application_id in (10, 7)
                 limit 1),
                p.alfUserName) as VALUE,
       ku.ID                   as USER_ID,
       UUID()                  as ID
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join em_socle.Profile p on p.user_id = u.id
where r.NAME = 'megalis'
having VALUE != '';

insert into USER_ATTRIBUTE (NAME, VALUE, USER_ID, ID)
select 'alfUserNameMultiTenant' as NAME, p.alfUserName as VALUE, ku.ID as USER_ID, UUID() as ID
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join em_socle.Profile p on p.user_id = u.id
where r.NAME = 'megalis';

insert into USER_ATTRIBUTE (NAME, VALUE, USER_ID, ID)
select 'siren' as NAME, COALESCE(s.siren, '') as VALUE, ku.ID as USER_ID, UUID() as ID
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join em_socle.Profile p on p.user_id = u.id
         left join em_socle.AgentProfile ap on ap.id = p.id
         left join em_socle.OrganismDepartment od on ap.organismDepartment_id = od.id
         left join em_socle.Structure s on s.id = od.organism_id
where r.NAME = 'megalis';

insert into USER_ATTRIBUTE (NAME, VALUE, USER_ID, ID)
select 'role'  as NAME,
       ro.name as VALUE,
       ku.ID   as USER_ID,
       UUID()  as ID
from USER_ENTITY ku
         join REALM r on ku.REALM_ID = r.ID
         join em_socle.`User` u on ku.USERNAME = u.username
         join em_socle.Profile p on p.user_id = u.id
         join em_socle.Profile_Role pr on pr.profiles_id = p.id
         join em_socle.Role ro on ro.id = pr.roles_id
         join em_socle.Application a on ro.application_id = a.id
where r.NAME = 'megalis'
  and a.name = 'pub-open-data';

-- insertion dans la base keycloak des anciens mots de passe des utilisateurs repris depuis la base socle
insert into CREDENTIAL (ID, `TYPE`, USER_ID, CREATED_DATE, SECRET_DATA, CREDENTIAL_DATA, PRIORITY)
select UUID()                                                                          as ID,
       'password'                                                                      as `TYPE`,
       ku.ID                                                                           as USER_ID,
       ku.CREATED_TIMESTAMP                                                            as CREATED_DATE,
       CONCAT('{"value":"', eu.password, '","salt":"","additionalParameters":{}}')     as SECRET_DATA,
       '{"hashIterations":27500,"algorithm":"myec3-scrypt","additionalParameters":{}}' as CREDENTIAL_DATA,
       10                                                                              as PRIORITY
from USER_ENTITY ku
         join em_socle.`User` eu on ku.USERNAME = eu.username
         join REALM r on ku.REALM_ID = r.id
where r.name = 'megalis';

-- ajout d'une action 'UPDATE_PASSWORD' pour tous les utilisateurs repris depuis la base socle
-- (entrainera la demande de renouvellement du mot de passe à la prochaine connexion)
insert into USER_REQUIRED_ACTION (USER_ID, REQUIRED_ACTION)
select ku.ID,
       'UPDATE_PASSWORD'
from USER_ENTITY ku
         join em_socle.`User` eu on ku.USERNAME = eu.username
         join REALM r on ku.REALM_ID = r.id
where r.name = 'megalis';

commit;