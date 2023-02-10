create table if not exists AcronymsList
(
    id        bigint auto_increment
    primary key,
    available bit          not null,
    value     varchar(255) not null,
    constraint value
    unique (value)
    )
    charset = utf8;

create table if not exists Administrateur
(
    id                 int auto_increment,
    adminEC            varchar(2)   not null,
    admin_general      char         not null,
    admin_grpmt        char         not null,
    alerte_fax         char         not null,
    email              varchar(100) not null,
    has_helios         char         not null,
    login              varchar(100) not null,
    mdp                varchar(32)  not null,
    nom                varchar(100) not null,
    organisme          varchar(30)  null,
    original_login     varchar(100) not null,
    prenom             varchar(100) not null,
    tentatives_mdp     int          not null,
    text_avertissement text         not null,
    constraint id
    unique (id)
    )
    charset = utf8;

alter table Administrateur
    add primary key (id);

create table if not exists Application
(
    nbmaxLicenses bigint       null,
    id            bigint auto_increment
    primary key,
    externalId    bigint       null,
    label         varchar(255) null,
    name          varchar(255) not null,
    url           varchar(255) not null,
    pictoUrl      varchar(255) null,
    description   varchar(255) null
    )
    charset = utf8;

create table if not exists Competence
(
    id   bigint auto_increment
    primary key,
    name varchar(255) null
    );

create table if not exists ConnectionInfos
(
    id                            bigint auto_increment
    primary key,
    lastConnectionDate            datetime not null,
    meanTimeBetweenTwoConnections bigint   null,
    nbConnections                 int      not null
)
    charset = utf8;

create table if not exists Customer
(
    id                          bigint auto_increment
    primary key,
    externalId                  bigint       null,
    label                       varchar(255) null,
    name                        varchar(255) not null,
    assistanceUrl               varchar(255) null,
    authorizedToManageCompanies bit          not null,
    documentationUrl            varchar(255) null,
    email                       varchar(255) null,
    hotlinePhone                varchar(255) null,
    logoUrl                     varchar(255) null,
    portalUrl                   varchar(255) null
    )
    charset = utf8;

create table if not exists Customer_Application
(
    customers_id    bigint not null,
    applications_id bigint not null,
    constraint FKf67s8j9ecax4x1npwqpkn79fs
    foreign key (customers_id) references Customer (id),
    constraint FK7739E26FD8CDEA0
    foreign key (customers_id) references Customer (id),
    constraint FKrtdra8w8op0fqrit9q1h1mv9y
    foreign key (applications_id) references Application (id),
    constraint FK7739E26FFD5E086A
    foreign key (applications_id) references Application (id)
    )
    charset = utf8;

create table if not exists ExportCSV
(
    id          bigint auto_increment
    primary key,
    content     longblob     null,
    dateDemande datetime(6)  not null,
    dateExport  datetime(6)  null,
    etat        varchar(255) not null
    );

create table if not exists InseeBorough
(
    id       bigint auto_increment
    primary key,
    ar       bigint       not null,
    artmaj   varchar(255) null,
    artmin   varchar(255) null,
    cheflieu varchar(255) not null,
    dep      varchar(255) not null,
    ncc      varchar(255) not null,
    nccenr   varchar(255) not null,
    region   bigint       not null,
    tncc     bigint       not null
    )
    charset = utf8;

create table if not exists InseeCanton
(
    id         bigint auto_increment
    primary key,
    artmaj     varchar(255) null,
    artmin     varchar(255) null,
    burcentral varchar(255) not null,
    canton     bigint       not null,
    dep        varchar(255) not null,
    ncc        varchar(255) not null,
    nccenr     varchar(255) not null,
    region     bigint       not null,
    tncc       varchar(255) not null,
    typct      bigint       not null
    )
    charset = utf8;

create table if not exists InseeCounty
(
    dep      varchar(255) not null
    primary key,
    cheflieu varchar(255) not null,
    ncc      varchar(255) not null,
    nccenr   varchar(255) not null,
    region   bigint       not null,
    tncc     bigint       not null
    )
    charset = utf8;

create table if not exists InseeGeoCode
(
    id         bigint auto_increment
    primary key,
    externalId bigint       null,
    label      varchar(255) null,
    name       varchar(255) not null,
    inseeCode  varchar(255) not null,
    ar         bigint       null,
    com        varchar(255) not null,
    ct         bigint       null,
    dep        varchar(255) not null,
    reg        bigint       not null,
    postalCode varchar(255) not null
    )
    charset = utf8;

create table if not exists Department
(
    id                    bigint auto_increment
    primary key,
    externalId            bigint       null,
    label                 varchar(255) null,
    name                  varchar(255) not null,
    acronym               varchar(255) not null,
    canton                varchar(255) null,
    city                  varchar(255) not null,
    country               varchar(255) null,
    postalAddress         varchar(255) not null,
    postalCode            varchar(255) not null,
    description           varchar(255) null,
    email                 varchar(255) not null,
    fax                   varchar(255) null,
    phone                 varchar(255) null,
    siren                 varchar(255) null,
    website               varchar(255) null,
    insee_id              bigint       null,
    additionalInfoAddress varchar(255) null,
    insee                 varchar(255) null,
    localityAddress       varchar(255) null,
    createdDate           datetime(6)  null,
    createdUserId         bigint       null,
    constraint FKA9601F724E1538D9
    foreign key (insee_id) references InseeGeoCode (id)
    )
    charset = utf8;

create table if not exists InseeLegalCategory
(
    idInsee int          not null
    primary key,
    label   varchar(255) null,
    parent  int          not null
    )
    charset = utf8;

create table if not exists InseeRegion
(
    region   bigint       not null
    primary key,
    cheflieu varchar(255) not null,
    ncc      varchar(255) not null,
    nccenr   varchar(255) not null,
    tncc     bigint       not null
    )
    charset = utf8;

create table if not exists MpsUpdateJob
(
    id       bigint       not null
    primary key,
    priority varchar(255) not null,
    type     varchar(255) not null
    )
    charset = utf8;

create table if not exists ProfileType
(
    id    bigint auto_increment
    primary key,
    value varchar(255) null,
    constraint value
    unique (value)
    )
    charset = utf8;

create table if not exists RevisionInfo
(
    id           bigint auto_increment
    primary key,
    modifiedBy   varchar(255) null,
    revisionDate datetime     null,
    timestamp    bigint       null
    )
    charset = utf8;

create table if not exists AgentManagedApplication_AUD
(
    id                    bigint  not null,
    REV                   bigint  not null,
    REVTYPE               tinyint null,
    agentProfile_id       bigint  null,
    managedApplication_id bigint  null,
    organism_id           bigint  null,
    primary key (id, REV),
    constraint FKlup0yhp294myjrmx6qomlo9kj
    foreign key (REV) references RevisionInfo (id)
    );

create table if not exists Application_AUD
(
    nbMaxLicense  bigint       null,
    nbMaxLicenses bigint       null,
    id            bigint       not null,
    REV           bigint       not null,
    REVTYPE       tinyint      null,
    externalId    bigint       null,
    label         varchar(255) null,
    name          varchar(255) null,
    url           varchar(255) null,
    pictoUrl      varchar(255) null,
    description   varchar(255) null,
    primary key (id, REV),
    constraint FKj4tmv60rib8ts49jy6vnxe8hl
    foreign key (REV) references RevisionInfo (id),
    constraint FK7A3FC3012A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists Competence_AUD
(
    id      bigint       not null,
    REV     bigint       not null,
    REVTYPE tinyint      null,
    name    varchar(255) null,
    primary key (id, REV),
    constraint FKo44hsamj3alx8o5f9fas2ikw3
    foreign key (REV) references RevisionInfo (id)
    );

create table if not exists Department_AUD
(
    id                    bigint       not null,
    REV                   bigint       not null,
    REVTYPE               tinyint      null,
    externalId            bigint       null,
    label                 varchar(255) null,
    name                  varchar(255) null,
    acronym               varchar(255) null,
    canton                varchar(255) null,
    city                  varchar(255) null,
    country               varchar(255) null,
    postalAddress         varchar(255) null,
    postalCode            varchar(255) null,
    description           varchar(255) null,
    email                 varchar(255) null,
    fax                   varchar(255) null,
    phone                 varchar(255) null,
    siren                 varchar(255) null,
    website               varchar(255) null,
    insee_id              bigint       null,
    additionalInfoAddress varchar(255) null,
    insee                 varchar(255) null,
    localityAddress       varchar(255) null,
    createdDate           datetime(6)  null,
    createdUserId         bigint       null,
    primary key (id, REV),
    constraint FKq0903s8h95erqvmqdq1m3sjoq
    foreign key (REV) references RevisionInfo (id),
    constraint FKB4AB44432A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists CompanyDepartment_AUD
(
    id                  bigint       not null,
    REV                 bigint       not null,
    nic                 varchar(255) null,
    registerTown        varchar(255) null,
    departmentSize      int          null,
    company_id          bigint       null,
    parentDepartment_id bigint       null,
    primary key (id, REV),
    constraint FKny674ua3dt07as88irplpexcv
    foreign key (id, REV) references Department_AUD (id, REV),
    constraint FKF6FDB1E0B43CB19E
    foreign key (id, REV) references Department_AUD (id, REV)
    )
    charset = utf8;

create table if not exists Establishment_AUD
(
    id                     bigint       not null,
    REV                    bigint       not null,
    REVTYPE                tinyint      null,
    externalId             bigint       null,
    label                  varchar(255) null,
    name                   varchar(255) null,
    canton                 varchar(255) null,
    city                   varchar(255) null,
    country                varchar(255) null,
    postalAddress          varchar(255) null,
    postalCode             varchar(255) null,
    adminStateLastUpdated  datetime     null,
    adminStateValue        varchar(255) null,
    apeCode                varchar(255) null,
    apeNafLabel            varchar(255) null,
    email                  varchar(255) null,
    fax                    varchar(255) null,
    foreignIdentifier      bit          null,
    isHeadOffice           bit          null,
    lastUpdate             datetime     null,
    nationalID             varchar(255) null,
    nic                    varchar(255) null,
    phone                  varchar(255) null,
    siret                  varchar(255) null,
    company_id             bigint       null,
    insee_id               bigint       null,
    diffusableInformations bit          null,
    additionalInfoAddress  varchar(255) null,
    insee                  varchar(255) null,
    localityAddress        varchar(255) null,
    createdDate            datetime(6)  null,
    createdUserId          bigint       null,
    primary key (id, REV),
    constraint FKs4l01rn27wc8i743y5bxnw3yy
    foreign key (REV) references RevisionInfo (id),
    constraint FKD270EC482A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists FunctionalAccount_AUD
(
    id         bigint       not null,
    REV        bigint       not null,
    REVTYPE    tinyint      null,
    externalId bigint       null,
    label      varchar(255) null,
    name       varchar(255) null,
    enabled    bit          null,
    user_id    bigint       null,
    primary key (id, REV),
    constraint FKlhp5as7bvgch4nybeluvq2elf
    foreign key (REV) references RevisionInfo (id),
    constraint FKFD4C975B2A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists OrganismDepartment_AUD
(
    id                  bigint       not null,
    REV                 bigint       not null,
    abbreviation        varchar(255) null,
    organism_id         bigint       null,
    parentDepartment_id bigint       null,
    primary key (id, REV),
    constraint FKoploxp92iuivf9m2laq12frw8
    foreign key (id, REV) references Department_AUD (id, REV),
    constraint FK170799B5B43CB19E
    foreign key (id, REV) references Department_AUD (id, REV)
    )
    charset = utf8;

create table if not exists OrganismStatus_AUD
(
    id          bigint       not null,
    REV         bigint       not null,
    REVTYPE     tinyint      null,
    date        datetime(6)  null,
    status      varchar(255) null,
    organism_id bigint       null,
    primary key (id, REV),
    constraint FKjkn0r7pr75lo9173r9sokhjxs
    foreign key (REV) references RevisionInfo (id)
    );

create table if not exists Person_AUD
(
    id         bigint       not null,
    REV        bigint       not null,
    REVTYPE    tinyint      null,
    externalId bigint       null,
    label      varchar(255) null,
    name       varchar(255) null,
    civility   varchar(255) null,
    firstname  varchar(255) null,
    function   varchar(255) null,
    lastname   varchar(255) null,
    company_id bigint       null,
    email      varchar(255) null,
    phone      varchar(255) null,
    type       varchar(255) null,
    primary key (id, REV),
    constraint FK73i47d6449jycudmvi3pn6olo
    foreign key (REV) references RevisionInfo (id),
    constraint FK9F49F2C62A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists ProfileTypeRole_AUD
(
    id             bigint  not null,
    REV            bigint  not null,
    REVTYPE        tinyint null,
    defaultAdmin   bit     null,
    defaultBasic   bit     null,
    profileType_id bigint  null,
    role_id        bigint  null,
    primary key (id, REV),
    constraint FKn3wtyix9eklkp9ngy4s8erxr6
    foreign key (REV) references RevisionInfo (id),
    constraint FK1FBB0CCA2A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists ProfileType_AUD
(
    id      bigint       not null,
    REV     bigint       not null,
    REVTYPE tinyint      null,
    value   varchar(255) null,
    primary key (id, REV),
    constraint FK33s2vlp1gbuyogf2eki6hd4nj
    foreign key (REV) references RevisionInfo (id),
    constraint FKA0447CB42A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists Profile_AUD
(
    id                    bigint       not null,
    REV                   bigint       not null,
    REVTYPE               tinyint      null,
    externalId            bigint       null,
    label                 varchar(255) null,
    name                  varchar(255) null,
    canton                varchar(255) null,
    city                  varchar(255) null,
    country               varchar(255) null,
    postalAddress         varchar(255) null,
    postalCode            varchar(255) null,
    cellPhone             varchar(255) null,
    email                 varchar(255) null,
    enabled               bit          null,
    fax                   varchar(255) null,
    function              varchar(255) null,
    grade                 varchar(255) null,
    phone                 varchar(255) null,
    prefComMedia          varchar(255) null,
    profileType_id        bigint       null,
    user_id               bigint       null,
    alfUserName           varchar(255) null,
    technicalIdentifier   varchar(255) null,
    insee_id              bigint       null,
    additionalInfoAddress varchar(255) null,
    insee                 varchar(255) null,
    localityAddress       varchar(255) null,
    dashboard             text         null,
    createdDate           datetime(6)  null,
    createdUserId         bigint       null,
    primary key (id, REV),
    constraint FK67vyrm6uq0bb68pg87si82u4f
    foreign key (REV) references RevisionInfo (id),
    constraint FK6E0562DA2A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists AdminProfile_AUD
(
    id          bigint not null,
    REV         bigint not null,
    customer_id bigint null,
    primary key (id, REV),
    constraint FKmuwlf25x827auet942vshah68
    foreign key (id, REV) references Profile_AUD (id, REV),
    constraint FK1B45CC4BF5A0EE1B
    foreign key (id, REV) references Profile_AUD (id, REV)
    )
    charset = utf8;

create table if not exists AgentProfile_AUD
(
    id                    bigint not null,
    REV                   bigint not null,
    elected               bit    null,
    executive             bit    null,
    representative        bit    null,
    substitute            bit    null,
    organismDepartment_id bigint null,
    primary key (id, REV),
    constraint FKelxy360s8mpic2hf1f20cy3u9
    foreign key (id, REV) references Profile_AUD (id, REV),
    constraint FKF0A2BED5F5A0EE1B
    foreign key (id, REV) references Profile_AUD (id, REV)
    )
    charset = utf8;

create table if not exists EmployeeProfile_AUD
(
    id                   bigint       not null,
    REV                  bigint       not null,
    nic                  varchar(255) null,
    companyDepartment_id bigint       null,
    establishment_id     bigint       null,
    primary key (id, REV),
    constraint FK1mo9rqs0q568579d64y8stvlt
    foreign key (id, REV) references Profile_AUD (id, REV),
    constraint FKDDA8EB4CF5A0EE1B
    foreign key (id, REV) references Profile_AUD (id, REV)
    )
    charset = utf8;

create table if not exists Profile_Role_AUD
(
    REV         bigint  not null,
    profiles_id bigint  not null,
    roles_id    bigint  not null,
    REVTYPE     tinyint null,
    primary key (REV, profiles_id, roles_id),
    constraint FK1h3o9aphfd1e297052pl8bedu
    foreign key (REV) references RevisionInfo (id),
    constraint FKCB5BA15D2A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists ProjectAccount_AUD
(
    id       bigint       not null,
    REV      bigint       not null,
    email    varchar(255) null,
    login    varchar(255) null,
    password varchar(255) null,
    primary key (id, REV),
    constraint FK3wmhveyhvdw1n5dogfhbffpa2
    foreign key (id, REV) references FunctionalAccount_AUD (id, REV),
    constraint FKC90E38C5C83D5ADC
    foreign key (id, REV) references FunctionalAccount_AUD (id, REV)
    )
    charset = utf8;

create table if not exists Role
(
    id             bigint auto_increment
    primary key,
    externalId     bigint       null,
    label          varchar(255) null,
    name           varchar(255) not null,
    description    longtext     null,
    application_id bigint       not null,
    hidden         bit          null,
    constraint FK976y056p9c4c19pe6g1r5l5jw
    foreign key (application_id) references Application (id),
    constraint FK26F496FAEF1D1D
    foreign key (application_id) references Application (id)
    )
    charset = utf8;

create table if not exists ProfileTypeRole
(
    id             bigint auto_increment
    primary key,
    defaultAdmin   bit    not null,
    defaultBasic   bit    not null,
    profileType_id bigint not null,
    role_id        bigint not null,
    constraint FKos07p2rvmpla0n5yqbdrudefn
    foreign key (role_id) references Role (id),
    constraint FKA03F43794ACAA1F7
    foreign key (role_id) references Role (id),
    constraint FKpsrpfo6ceujmoq52u8fxea11j
    foreign key (profileType_id) references ProfileType (id),
    constraint FKA03F4379998530CE
    foreign key (profileType_id) references ProfileType (id)
    )
    charset = utf8;

create table if not exists Role_AUD
(
    id             bigint       not null,
    REV            bigint       not null,
    REVTYPE        tinyint      null,
    externalId     bigint       null,
    label          varchar(255) null,
    name           varchar(255) null,
    description    longtext     null,
    application_id bigint       null,
    hidden         bit          null,
    primary key (id, REV),
    constraint FKfu0rgyvirap8vjy703jdojvp0
    foreign key (REV) references RevisionInfo (id),
    constraint FKF3FAE7672A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists StructureType
(
    id    bigint auto_increment
    primary key,
    value varchar(255) null,
    constraint value
    unique (value)
    )
    charset = utf8;

create table if not exists Structure
(
    id                    bigint auto_increment
    primary key,
    externalId            bigint       null,
    label                 varchar(255) null,
    name                  varchar(255) not null,
    acronym               varchar(3)   not null,
    canton                varchar(255) null,
    city                  varchar(255) not null,
    country               varchar(255) null,
    postalAddress         varchar(255) not null,
    postalCode            varchar(255) not null,
    description           longtext     null,
    email                 varchar(255) not null,
    enabled               bit          not null,
    fax                   varchar(255) null,
    iconUrl               varchar(255) null,
    logoUrl               varchar(255) null,
    phone                 varchar(255) null,
    siren                 varchar(255) null,
    website               varchar(255) null,
    workforce             int          null,
    structureType_id      bigint       null,
    tenantIdentifier      varchar(255) null,
    insee_id              bigint       null,
    additionalInfoAddress varchar(255) null,
    insee                 varchar(255) null,
    localityAddress       varchar(255) null,
    createdDate           datetime(6)  null,
    createdUserId         bigint       null,
    constraint tenantIdentifier
    unique (tenantIdentifier),
    constraint FK800F4D534E1538D9
    foreign key (insee_id) references InseeGeoCode (id),
    constraint FKmgrs4bdp9ikjrivakps1si0ge
    foreign key (structureType_id) references StructureType (id),
    constraint FK800F4D53E28F1F8E
    foreign key (structureType_id) references StructureType (id)
    )
    charset = utf8;

create table if not exists Company
(
    RCS                    bit          not null,
    RM                     bit          not null,
    apeCode                varchar(255) not null,
    apeNafLabel            varchar(255) null,
    foreignIdentifier      bit          null,
    insee                  varchar(255) null,
    legalCategory          varchar(255) not null,
    nationalID             varchar(255) null,
    nic                    varchar(255) null,
    registrationCountry    varchar(255) not null,
    id                     bigint       not null
    primary key,
    adminStateLastUpdated  datetime     null,
    adminStateValue        varchar(255) null,
    companyAcronym         varchar(255) null,
    companyCategory        int          null,
    creationDate           datetime     null,
    lastUpdate             datetime     null,
    radiationDate          datetime     null,
    diffusableInformations bit          null,
    constraint FKjoh0dn9wkorgnxb1dfk123ld6
    foreign key (id) references Structure (id),
    constraint FK9BDFD45DCB52A4B1
    foreign key (id) references Structure (id)
    )
    charset = utf8;

create table if not exists CompanyDepartment
(
    nic                 varchar(255) null,
    registerTown        varchar(255) null,
    departmentSize      int          null,
    id                  bigint       not null
    primary key,
    company_id          bigint       not null,
    parentDepartment_id bigint       null,
    constraint FK13dfnxym7rayvg85o2evko3g5
    foreign key (company_id) references Company (id),
    constraint FK6esgtlxvspnwljatbi600g9yp
    foreign key (id) references Department (id),
    constraint FKB59BE38FC6862AAA
    foreign key (id) references Department (id),
    constraint FKB59BE38FDA605B7D
    foreign key (company_id) references Company (id),
    constraint FKhthlgyomu4juu2pdu5aixb3lt
    foreign key (parentDepartment_id) references CompanyDepartment (id),
    constraint FKB59BE38F31F5AB90
    foreign key (parentDepartment_id) references CompanyDepartment (id)
    )
    charset = utf8;

create table if not exists Establishment
(
    id                     bigint auto_increment
    primary key,
    externalId             bigint       null,
    label                  varchar(255) null,
    name                   varchar(255) not null,
    canton                 varchar(255) null,
    city                   varchar(255) not null,
    country                varchar(255) null,
    postalAddress          varchar(255) not null,
    postalCode             varchar(255) not null,
    adminStateLastUpdated  datetime     null,
    adminStateValue        varchar(255) null,
    apeCode                varchar(255) not null,
    apeNafLabel            varchar(255) null,
    email                  varchar(255) not null,
    fax                    varchar(255) null,
    foreignIdentifier      bit          null,
    isHeadOffice           bit          not null,
    lastUpdate             datetime     null,
    nationalID             varchar(255) null,
    nic                    varchar(255) null,
    phone                  varchar(255) null,
    siret                  varchar(255) null,
    company_id             bigint       not null,
    insee_id               bigint       null,
    diffusableInformations bit          not null,
    additionalInfoAddress  varchar(255) null,
    insee                  varchar(255) null,
    localityAddress        varchar(255) null,
    createdDate            datetime(6)  null,
    createdUserId          bigint       null,
    constraint FKD75C91F74E1538D9
    foreign key (insee_id) references InseeGeoCode (id),
    constraint FKs2roe4w7khecnv9a59fx4kf3h
    foreign key (company_id) references Company (id),
    constraint FKD75C91F7DA605B7D
    foreign key (company_id) references Company (id)
    )
    charset = utf8;

create index if not exists siret_idx
    on Establishment (siret);

create table if not exists Organism
(
    apeCode             varchar(255) null,
    article             varchar(255) null,
    beginMembershipDate datetime     null,
    budget              int          null,
    college             varchar(255) null,
    contributionAmount  int          null,
    endMembershipDate   datetime     null,
    legalCategory       varchar(255) not null,
    membership          bit          not null,
    memberStatus        varchar(255) null,
    officialPopulation  int          null,
    id                  bigint       not null
    primary key,
    customer_id         bigint       not null,
    nic                 varchar(255) null,
    apiKey              varchar(255) null,
    ideoSignatureDate   datetime(6)  null,
    constraint FK6hid4s2lvyhs0gk2a0nyews17
    foreign key (customer_id) references Customer (id),
    constraint FK5250E4F26E354277
    foreign key (customer_id) references Customer (id),
    constraint FKey5ktyrimms3mqlvm18xyw6pr
    foreign key (id) references Structure (id),
    constraint FK5250E4F2CB52A4B1
    foreign key (id) references Structure (id)
    )
    charset = utf8;

create table if not exists OrganismDepartment
(
    abbreviation        varchar(255) null,
    id                  bigint       not null
    primary key,
    organism_id         bigint       not null,
    parentDepartment_id bigint       null,
    constraint FKat75obphxku9ddrr37nq0pgvh
    foreign key (organism_id) references Organism (id),
    constraint FKC714FDE4D2842DF7
    foreign key (organism_id) references Organism (id),
    constraint FKj9e7yplpbh6pfm24a97082et9
    foreign key (id) references Department (id),
    constraint FKC714FDE4C6862AAA
    foreign key (id) references Department (id),
    constraint FKqyfkbe0n97tcnc3t6d2s4hxib
    foreign key (parentDepartment_id) references OrganismDepartment (id),
    constraint FKC714FDE42F34FFBF
    foreign key (parentDepartment_id) references OrganismDepartment (id)
    )
    charset = utf8;

create table if not exists OrganismStatus
(
    id          bigint auto_increment
    primary key,
    date        datetime(6)  not null,
    status      varchar(255) null,
    organism_id bigint       null,
    constraint FKkbxfmvx28kcnf1nqx2sfhg0fg
    foreign key (organism_id) references Organism (id)
    );

create table if not exists Person
(
    id         bigint auto_increment
    primary key,
    externalId bigint       null,
    label      varchar(255) null,
    name       varchar(255) not null,
    civility   varchar(255) null,
    firstname  varchar(255) not null,
    function   varchar(255) null,
    lastname   varchar(255) not null,
    company_id bigint       null,
    email      varchar(255) null,
    phone      varchar(255) null,
    type       varchar(255) not null,
    constraint FK8akppqvvi9wa7bb0kvyv0tlu9
    foreign key (company_id) references Company (id),
    constraint FK8E488775DA605B7D
    foreign key (company_id) references Company (id)
    )
    charset = utf8;

create index if not exists Structure_externalId
    on Structure (externalId);

create index if not exists siren_idx
    on Structure (siren);

create table if not exists StructureTypeApplication
(
    id                  bigint auto_increment
    primary key,
    defaultSubscription bit    not null,
    manageableRoles     bit    not null,
    multipleRoles       bit    not null,
    subscribable        bit    not null,
    application_id      bigint not null,
    structureType_id    bigint not null,
    constraint FK4qnr1jb532teyp4e4j5vicfka
    foreign key (application_id) references Application (id),
    constraint FK7615184i1epw58qcsu7o48i00
    foreign key (structureType_id) references StructureType (id),
    constraint FKE261EFE3E28F1F8E
    foreign key (structureType_id) references StructureType (id),
    constraint FKE261EFE3FAEF1D1D
    foreign key (application_id) references Application (id)
    )
    charset = utf8;

create table if not exists StructureTypeApplication_AUD
(
    id                  bigint  not null,
    REV                 bigint  not null,
    REVTYPE             tinyint null,
    defaultSubscription bit     null,
    manageableRoles     bit     null,
    multipleRoles       bit     null,
    subscribable        bit     null,
    application_id      bigint  null,
    structureType_id    bigint  null,
    primary key (id, REV),
    constraint FK4cb2mourxn40oev8nk9bgs1rj
    foreign key (REV) references RevisionInfo (id),
    constraint FKFE7D74342A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists StructureType_AUD
(
    id      bigint       not null,
    REV     bigint       not null,
    REVTYPE tinyint      null,
    value   varchar(255) null,
    primary key (id, REV),
    constraint FKa30hhstp9jo60ngy7fse3wduv
    foreign key (REV) references RevisionInfo (id),
    constraint FK84313E7E2A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists Structure_AUD
(
    id                    bigint       not null,
    REV                   bigint       not null,
    REVTYPE               tinyint      null,
    externalId            bigint       null,
    label                 varchar(255) null,
    name                  varchar(255) null,
    acronym               varchar(3)   null,
    canton                varchar(255) null,
    city                  varchar(255) null,
    country               varchar(255) null,
    postalAddress         varchar(255) null,
    postalCode            varchar(255) null,
    description           longtext     null,
    email                 varchar(255) null,
    enabled               bit          null,
    fax                   varchar(255) null,
    iconUrl               varchar(255) null,
    logoUrl               varchar(255) null,
    phone                 varchar(255) null,
    siren                 varchar(255) null,
    website               varchar(255) null,
    workforce             int          null,
    structureType_id      bigint       null,
    tenantIdentifier      varchar(255) null,
    insee_id              bigint       null,
    additionalInfoAddress varchar(255) null,
    insee                 varchar(255) null,
    localityAddress       varchar(255) null,
    createdDate           datetime(6)  null,
    createdUserId         bigint       null,
    primary key (id, REV),
    constraint FKo3a6musbb0cg9dbukvenvfxdt
    foreign key (REV) references RevisionInfo (id),
    constraint FK225E99A42A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists Company_AUD
(
    id                     bigint       not null,
    REV                    bigint       not null,
    RCS                    bit          null,
    RM                     bit          null,
    apeCode                varchar(255) null,
    apeNafLabel            varchar(255) null,
    foreignIdentifier      bit          null,
    insee                  varchar(255) null,
    legalCategory          varchar(255) null,
    nationalID             varchar(255) null,
    nic                    varchar(255) null,
    registrationCountry    varchar(255) null,
    adminStateLastUpdated  datetime     null,
    adminStateValue        varchar(255) null,
    companyAcronym         varchar(255) null,
    companyCategory        int          null,
    creationDate           datetime     null,
    lastUpdate             datetime     null,
    radiationDate          datetime     null,
    diffusableInformations bit          null,
    primary key (id, REV),
    constraint FKau4ekh83j1sqqbnga3l66qp5
    foreign key (id, REV) references Structure_AUD (id, REV),
    constraint FK45208BAE2A9ED025
    foreign key (id, REV) references Structure_AUD (id, REV)
    )
    charset = utf8;

create table if not exists Organism_AUD
(
    id                  bigint       not null,
    REV                 bigint       not null,
    apeCode             varchar(255) null,
    article             varchar(255) null,
    beginMembershipDate datetime     null,
    budget              int          null,
    college             varchar(255) null,
    contributionAmount  int          null,
    endMembershipDate   datetime     null,
    legalCategory       varchar(255) null,
    membership          bit          null,
    memberStatus        varchar(255) null,
    officialPopulation  int          null,
    customer_id         bigint       null,
    nic                 varchar(255) null,
    apiKey              varchar(255) null,
    ideoSignatureDate   datetime(6)  null,
    primary key (id, REV),
    constraint FKc2f52icyl3ps6utmthqm8qra0
    foreign key (id, REV) references Structure_AUD (id, REV),
    constraint FK44BD49C32A9ED025
    foreign key (id, REV) references Structure_AUD (id, REV)
    )
    charset = utf8;

create table if not exists Structure_Application
(
    structures_id   bigint not null,
    applications_id bigint not null,
    constraint FKh8x8xif84oy3ur8djyylv2ft8
    foreign key (structures_id) references Structure (id),
    constraint FK39F8F444BE1B8BF0
    foreign key (structures_id) references Structure (id),
    constraint FKjee1migal6p3iq89guiunnsnv
    foreign key (applications_id) references Application (id),
    constraint FK39F8F444FD5E086A
    foreign key (applications_id) references Application (id)
    )
    charset = utf8;

create table if not exists Structure_Application_AUD
(
    REV             bigint  not null,
    structures_id   bigint  not null,
    applications_id bigint  not null,
    REVTYPE         tinyint null,
    primary key (REV, structures_id, applications_id),
    constraint FKejek8tex710vxpp7pvq06dvla
    foreign key (REV) references RevisionInfo (id),
    constraint FKF04960152A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists Structure_Application_Info
(
    applications_id bigint not null,
    structures_id   bigint not null,
    nbMaxLicenses   bigint null,
    primary key (applications_id, structures_id),
    constraint FKc1gbf38df64ynksrej4618x7r
    foreign key (structures_id) references Structure (id),
    constraint FKgdj1dg9s2gqgeryp9v8xrv7bg
    foreign key (applications_id) references Application (id)
    );

create table if not exists Structure_Application_Info_AUD
(
    applications_id bigint  not null,
    structures_id   bigint  not null,
    REV             bigint  not null,
    REVTYPE         tinyint null,
    nbMaxLicenses   bigint  null,
    primary key (applications_id, structures_id, REV),
    constraint FKq8338hhdxb24wpb4s1eshl5gh
    foreign key (REV) references RevisionInfo (id)
    );

create table if not exists Structure_Structure
(
    parentStructures_id bigint not null,
    childStructures_id  bigint not null,
    constraint FK8lgqlbxqcuw43mp6fapl8eop7
    foreign key (childStructures_id) references Structure (id),
    constraint FK308949672F8FC454
    foreign key (childStructures_id) references Structure (id),
    constraint FKs6897ceo3ca4iq9069a9gtqam
    foreign key (parentStructures_id) references Structure (id),
    constraint FK3089496712E56786
    foreign key (parentStructures_id) references Structure (id)
    )
    charset = utf8;

create table if not exists Structure_Structure_AUD
(
    REV                 bigint  not null,
    parentStructures_id bigint  not null,
    childStructures_id  bigint  not null,
    REVTYPE             tinyint null,
    primary key (REV, parentStructures_id, childStructures_id),
    constraint FKo14n6qr6bfq9medhw2becr63o
    foreign key (REV) references RevisionInfo (id),
    constraint FKCE946BB82A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists SviProfile
(
    id bigint auto_increment
    primary key
)
    charset = utf8;

create table if not exists SviProfile_AUD
(
    id      bigint  not null,
    REV     bigint  not null,
    REVTYPE tinyint null,
    primary key (id, REV),
    constraint FKtbdjq4xlau243texw5slqf149
    foreign key (REV) references RevisionInfo (id),
    constraint FKC12F94D42A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists SynchroIdentifiantExterne
(
    id             bigint auto_increment
    primary key,
    acronyme       varchar(255) null,
    idAppliExterne bigint       null,
    idSocle        bigint       null,
    typeRessource  varchar(255) not null,
    application_id bigint       not null,
    constraint FKr3nu1kjljrla52ywc75xj5aiv
    foreign key (application_id) references Application (id)
    );

create index if not exists acronyme_idx
    on SynchroIdentifiantExterne (acronyme);

create index if not exists idsdm_idx
    on SynchroIdentifiantExterne (idAppliExterne);

create table if not exists SynchroIdentifiantExterneDelta
(
    id             bigint auto_increment
    primary key,
    idAppliExterne bigint                      null,
    idSocle        bigint                      null,
    json           varchar(10000) charset utf8 null,
    typeRessource  varchar(255)                not null,
    application_id bigint                      not null,
    constraint FK6xwr8j090oeu06o4ypeju7fy8
    foreign key (application_id) references Application (id)
    );

create table if not exists SynchronizationError
(
    id         bigint auto_increment
    primary key,
    methodType varchar(255) not null,
    nbAttempts int          not null,
    resourceId bigint       not null
    )
    charset = utf8;

create table if not exists SynchronizationFilter
(
    id                       bigint auto_increment
    primary key,
    allApplicationsDisplayed bit not null,
    allRolesDisplayed        bit not null
)
    charset = utf8;

create table if not exists SynchronizationQueue
(
    id                     bigint auto_increment
    primary key,
    resourceId             bigint       not null,
    resourceType           varchar(255) not null,
    sendingApplication     varchar(255) null,
    synchronizationJobType varchar(255) not null
    )
    charset = utf8;

create table if not exists SynchronizationSubscription
(
    id                       bigint auto_increment
    primary key,
    resourceLabel            varchar(255)     not null,
    uri                      varchar(255)     not null,
    application_id           bigint           not null,
    synchronizationFilter_id bigint           not null,
    certificatePassword      varchar(255)     null,
    certificateUri           varchar(255)     null,
    https                    bit default b'0' null,
    constraint FKs4icrohm4w8fe63wh4rodotw8
    foreign key (synchronizationFilter_id) references SynchronizationFilter (id),
    constraint FKF360F77DDBC1CFB9
    foreign key (synchronizationFilter_id) references SynchronizationFilter (id),
    constraint FKtrr7fcdjv3ehj0l7kjgva0c59
    foreign key (application_id) references Application (id),
    constraint FKF360F77DFAEF1D1D
    foreign key (application_id) references Application (id)
    )
    charset = utf8;

create table if not exists SynchronizationLog
(
    id                             bigint auto_increment
    primary key,
    applicationName                varchar(255) not null,
    errorCodeType                  varchar(255) null,
    errorLabel                     varchar(255) null,
    errorMessage                   text         null,
    httpCode                       int          not null,
    httpStatus                     varchar(255) not null,
    isFinal                        bit          not null,
    methodType                     varchar(255) not null,
    nbAttempts                     int          not null,
    resourceId                     bigint       not null,
    resourceType                   varchar(255) not null,
    responseMessageResourceId      bigint       null,
    sendingApplication             varchar(255) null,
    statut                         varchar(255) not null,
    synchronizationDate            datetime     not null,
    synchronizationType            varchar(255) not null,
    synchronizationSubscription_id bigint       not null,
    constraint FK7b41cd3sggircikxxg0jbmqek
    foreign key (synchronizationSubscription_id) references SynchronizationSubscription (id),
    constraint FKB9B958E42256B659
    foreign key (synchronizationSubscription_id) references SynchronizationSubscription (id)
    )
    charset = utf8;

create table if not exists SynchronizationInitial
(
    id                       bigint auto_increment
    primary key,
    initialSynchronizationId bigint not null,
    synchronizationLog_id    bigint not null,
    constraint FKmacemqj3tbhmls60y3d26u8xa
    foreign key (synchronizationLog_id) references SynchronizationLog (id),
    constraint FK79C53CE46712829B
    foreign key (synchronizationLog_id) references SynchronizationLog (id)
    )
    charset = utf8;

create table if not exists SynchronizationSubscription_copy_with_organism
(
    id                       bigint auto_increment
    primary key,
    resourceLabel            varchar(255)     not null,
    uri                      varchar(255)     not null,
    application_id           bigint           not null,
    synchronizationFilter_id bigint           not null,
    certificatePassword      varchar(255)     null,
    certificateUri           varchar(255)     null,
    https                    bit default b'0' null,
    constraint SynchronizationSubscription_copy_with_organism_ibfk_1
    foreign key (synchronizationFilter_id) references SynchronizationFilter (id),
    constraint SynchronizationSubscription_copy_with_organism_ibfk_2
    foreign key (application_id) references Application (id),
    constraint SynchronizationSubscription_copy_with_organism_ibfk_3
    foreign key (synchronizationFilter_id) references SynchronizationFilter (id),
    constraint SynchronizationSubscription_copy_with_organism_ibfk_4
    foreign key (application_id) references Application (id)
    )
    charset = utf8;

create index if not exists FKF360F77DDBC1CFB9
    on SynchronizationSubscription_copy_with_organism (synchronizationFilter_id);

create index if not exists FKF360F77DFAEF1D1D
    on SynchronizationSubscription_copy_with_organism (application_id);

create table if not exists TEMP_TABLE_CREATION_DATE
(
    user_id      bigint null,
    creationDate date   null
);

create table if not exists TMP_EMP_ETAB
(
    empid      bigint           not null,
    etabid     bigint default 0 not null,
    compid     bigint           not null,
    new_etabid binary(0)        null
    );

create table if not exists TMP_EMP_ETAB2
(
    empid      bigint           not null,
    etabid     bigint default 0 not null,
    new_etabid bigint           null
);

create table if not exists User
(
    id                     bigint auto_increment
    primary key,
    externalId             bigint        null,
    label                  varchar(255)  null,
    name                   varchar(255)  not null,
    certificate            text          null,
    civility               varchar(255)  null,
    connectionAttempts     int default 0 not null,
    enabled                bit           not null,
    firstname              varchar(255)  not null,
    lastname               varchar(255)  not null,
    password               varchar(255)  null,
    username               varchar(255)  not null,
    connectionInfos_id     bigint        null,
    sviProfile_id          bigint        null,
    controlKeyNewPassword  varchar(255)  null,
    expirationDatePassword date          null,
    modifDatePassword      datetime      null,
    birthCountry           varchar(255)  null,
    birthDate              date          null,
    birthPlace             varchar(255)  null,
    creationDate           datetime(6)   null,
    constraint username
    unique (username),
    constraint FK26gkrdrt1y2bg6epu2u6qs1hg
    foreign key (sviProfile_id) references SviProfile (id),
    constraint FK285FEB1575F0F7
    foreign key (sviProfile_id) references SviProfile (id),
    constraint FKdr302415v92dc1auhwaq0y011
    foreign key (connectionInfos_id) references ConnectionInfos (id),
    constraint FK285FEBFBC6381D
    foreign key (connectionInfos_id) references ConnectionInfos (id)
    )
    charset = utf8;

create table if not exists FunctionalAccount
(
    id         bigint auto_increment
    primary key,
    externalId bigint       null,
    label      varchar(255) null,
    name       varchar(255) not null,
    enabled    bit          not null,
    user_id    bigint       not null,
    constraint FKd0s4wswibpxslujr2rse5rton
    foreign key (user_id) references User (id),
    constraint FKFB16BE8AEFF565D7
    foreign key (user_id) references User (id)
    )
    charset = utf8;

create table if not exists Profile
(
    id                    bigint auto_increment
    primary key,
    externalId            bigint       null,
    label                 varchar(255) null,
    name                  varchar(255) not null,
    canton                varchar(255) null,
    city                  varchar(255) not null,
    country               varchar(255) null,
    postalAddress         varchar(255) not null,
    postalCode            varchar(255) not null,
    cellPhone             varchar(255) null,
    email                 varchar(255) not null,
    enabled               bit          not null,
    fax                   varchar(255) null,
    function              varchar(255) null,
    grade                 varchar(255) null,
    phone                 varchar(255) null,
    prefComMedia          varchar(255) null,
    profileType_id        bigint       null,
    user_id               bigint       not null,
    alfUserName           varchar(255) null,
    technicalIdentifier   varchar(255) null,
    insee_id              bigint       null,
    additionalInfoAddress varchar(255) null,
    insee                 varchar(255) null,
    localityAddress       varchar(255) null,
    dashboard             text         null,
    createdDate           datetime(6)  null,
    createdUserId         bigint       null,
    constraint alfUserName
    unique (alfUserName),
    constraint FK50C721894E1538D9
    foreign key (insee_id) references InseeGeoCode (id),
    constraint FK9ke43amidbypdepae19k20a12
    foreign key (user_id) references User (id),
    constraint FK50C72189EFF565D7
    foreign key (user_id) references User (id),
    constraint FKnb1gnnrx9m2apu1r8il1isv06
    foreign key (profileType_id) references ProfileType (id),
    constraint FK50C72189998530CE
    foreign key (profileType_id) references ProfileType (id)
    )
    charset = utf8;

create table if not exists AdminProfile
(
    id          bigint not null
    primary key,
    customer_id bigint not null,
    constraint FK3nvxmupuxwi1u31t9nccd1n1q
    foreign key (id) references Profile (id),
    constraint FKE44FEB7AC1CE2DA7
    foreign key (id) references Profile (id),
    constraint FKgxku9rivj2x7a1xsm4ov4rvqt
    foreign key (customer_id) references Customer (id),
    constraint FKE44FEB7A6E354277
    foreign key (customer_id) references Customer (id)
    )
    charset = utf8;

create table if not exists AgentProfile
(
    elected               bit    not null,
    executive             bit    not null,
    representative        bit    not null,
    substitute            bit    not null,
    id                    bigint not null
    primary key,
    organismDepartment_id bigint not null,
    constraint FK1fcrmyod23pl5ugdk0irwynqx
    foreign key (id) references Profile (id),
    constraint FK554FB304C1CE2DA7
    foreign key (id) references Profile (id),
    constraint FK5pvac97iasiyapqxwk6dydrbi
    foreign key (organismDepartment_id) references OrganismDepartment (id),
    constraint FK554FB30468740897
    foreign key (organismDepartment_id) references OrganismDepartment (id)
    )
    charset = utf8;

create table if not exists AgentManagedApplication
(
    id                    bigint auto_increment
    primary key,
    agentProfile_id       bigint null,
    managedApplication_id bigint null,
    organism_id           bigint null,
    constraint FK6vvuu2ei4go6a8iww33321or0
    foreign key (organism_id) references Organism (id),
    constraint FKewehw8h8tqd663x1av830uib0
    foreign key (managedApplication_id) references Application (id),
    constraint FKg73x1w1g7y53drh306n48lhfr
    foreign key (agentProfile_id) references AgentProfile (id)
    );

create table if not exists EmployeeProfile
(
    nic                  varchar(255) null,
    id                   bigint       not null
    primary key,
    companyDepartment_id bigint       not null,
    establishment_id     bigint       null,
    constraint FKfidwic4lhskhip45r6gvxqwd3
    foreign key (establishment_id) references Establishment (id),
    constraint FKCD54B2FBEF5646FD
    foreign key (establishment_id) references Establishment (id),
    constraint FKld04mjbjyeanhm8m4nvqo07xw
    foreign key (id) references Profile (id),
    constraint FKCD54B2FBC1CE2DA7
    foreign key (id) references Profile (id),
    constraint FKn5j4s6hfyvcb3hoih8ruvcgo4
    foreign key (companyDepartment_id) references CompanyDepartment (id),
    constraint FKCD54B2FB7071869D
    foreign key (companyDepartment_id) references CompanyDepartment (id)
    )
    charset = utf8;

create table if not exists Profile_Role
(
    profiles_id bigint not null,
    roles_id    bigint not null,
    constraint FKkk2wg94c41cwblkoay451510j
    foreign key (roles_id) references Role (id),
    constraint FK52AF198CE8E31A50
    foreign key (roles_id) references Role (id),
    constraint FKsm8870k6y1rpf610ysllt0fne
    foreign key (profiles_id) references Profile (id),
    constraint FK52AF198CCC73661C
    foreign key (profiles_id) references Profile (id)
    )
    charset = utf8;

create table if not exists ProjectAccount
(
    email    varchar(255) not null,
    login    varchar(255) not null,
    password varchar(255) not null,
    id       bigint       not null
    primary key,
    constraint login
    unique (login),
    constraint FK89efcqkbuf476pq8hgeyuwpk2
    foreign key (id) references FunctionalAccount (id),
    constraint FKAEE5A4F4E95922E8
    foreign key (id) references FunctionalAccount (id)
    )
    charset = utf8;

create table if not exists User_AUD
(
    id                     bigint       not null,
    REV                    bigint       not null,
    REVTYPE                tinyint      null,
    externalId             bigint       null,
    label                  varchar(255) null,
    name                   varchar(255) null,
    certificate            text         null,
    civility               varchar(255) null,
    connectionAttempts     int          null,
    enabled                bit          null,
    firstname              varchar(255) null,
    lastname               varchar(255) null,
    password               varchar(255) null,
    username               varchar(255) null,
    connectionInfos_id     bigint       null,
    sviProfile_id          bigint       null,
    controlKeyNewPassword  varchar(255) null,
    expirationDatePassword date         null,
    modifDatePassword      datetime     null,
    birthCountry           varchar(255) null,
    birthDate              date         null,
    birthPlace             varchar(255) null,
    creationDate           datetime(6)  null,
    primary key (id, REV),
    constraint FKe0vq076b91mguawapyxglxsmo
    foreign key (REV) references RevisionInfo (id),
    constraint FKF3FCA03C2A605C11
    foreign key (REV) references RevisionInfo (id)
    )
    charset = utf8;

create table if not exists competence_agentprofile
(
    agentProfile_id bigint not null,
    competence_id   bigint not null,
    primary key (agentProfile_id, competence_id),
    constraint FKb4ekeu3xgdphffnqfcn557954
    foreign key (competence_id) references Competence (id),
    constraint FKrs0pp7pwuguqiynm39ol4yg6i
    foreign key (agentProfile_id) references AgentProfile (id)
    );

create table if not exists competence_agentprofile_AUD
(
    REV             bigint  not null,
    agentProfile_id bigint  not null,
    competence_id   bigint  not null,
    REVTYPE         tinyint null,
    primary key (REV, agentProfile_id, competence_id),
    constraint FKq75tpmxajkjnq5b0vcyc5jry8
    foreign key (REV) references RevisionInfo (id)
    );

create table if not exists tempTableCompany
(
    idSdm      bigint null,
    id_externe bigint null
);

create index if not exists tempTableCompany_idExterne
    on tempTableCompany (id_externe);

create table if not exists tempTableEstablishment
(
    id_etablissement   bigint       null,
    id_entreprise      bigint       null,
    code_etablissement varchar(255) null,
    id_externe         bigint       null
    );

create index if not exists tempTableEstablishment_idExterne
    on tempTableEstablishment (id_externe);

