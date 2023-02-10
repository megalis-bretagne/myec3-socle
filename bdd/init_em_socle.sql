create table if not exists acte_old
(
    id             int auto_increment
    primary key,
    name           varchar(300) not null,
    url            varchar(500) not null,
    path           varchar(500) not null,
    publication_id int          null,
    hash           varchar(65)  null
    );

create index if not exists publication_id
    on acte_old (publication_id);

create table if not exists celery_taskmeta
(
    id        int          not null
    primary key,
    task_id   varchar(155) null,
    status    varchar(50)  null,
    result    blob         null,
    date_done datetime     null,
    traceback text         null,
    name      varchar(155) null,
    args      blob         null,
    kwargs    blob         null,
    worker    varchar(155) null,
    retries   int          null,
    queue     varchar(155) null,
    constraint task_id
    unique (task_id)
    );

create table if not exists celery_tasksetmeta
(
    id         int          not null
    primary key,
    taskset_id varchar(155) null,
    result     blob         null,
    date_done  datetime     null,
    constraint taskset_id
    unique (taskset_id)
    );

create table if not exists entite_pastell_ag
(
    id           int auto_increment
    primary key,
    siren        varchar(9)   not null,
    id_e         int          not null,
    denomination varchar(256) null
    );

create table if not exists parametrage
(
    id                           int auto_increment
    primary key,
    siren                        varchar(9)             not null,
    open_data_active             tinyint(1) default 0   not null,
    publication_data_gouv_active tinyint(1) default 0   not null,
    uid_data_gouv                varchar(256)           null,
    api_key_data_gouv            varchar(256)           null,
    created_at                   datetime               not null,
    modified_at                  datetime               not null,
    publication_udata_active     tinyint(1) default 1   not null,
    nic                          varchar(5) default '0' not null,
    denomination                 varchar(256)           null,
    constraint parametrage_UN
    unique (siren)
    );

create table if not exists pj_acte_old
(
    id             int auto_increment
    primary key,
    name           varchar(200) not null,
    url            varchar(500) not null,
    path           varchar(500) not null,
    publication_id int          null,
    hash           varchar(65)  null
    );

create index if not exists publication_id
    on pj_acte_old (publication_id);

create table if not exists publication
(
    id                    int auto_increment
    primary key,
    numero_de_lacte       varchar(20)            not null,
    objet                 varchar(256)           not null,
    siren                 varchar(9)             not null,
    publication_open_data varchar(1) default '0' not null,
    date_de_lacte         datetime               not null,
    classification_code   varchar(10)            not null,
    classification_nom    varchar(100)           not null,
    acte_nature           varchar(50)            not null,
    envoi_depot           varchar(50)            not null,
    date_budget           varchar(10)            null,
    est_masque            tinyint(1) default 0   not null,
    etat                  varchar(1) default '0' not null,
    created_at            datetime               not null,
    modified_at           datetime               not null,
    est_supprime          tinyint(1) default 0   null,
    date_publication      datetime               null,
    nature_autre_detail   varchar(255)           null
    );

create table if not exists acte
(
    id             int auto_increment
    primary key,
    name           varchar(300) not null,
    url            varchar(500) not null,
    path           varchar(500) not null,
    publication_id int          null,
    hash           varchar(65)  null,
    constraint acte_ibfk_1
    foreign key (publication_id) references publication (id)
    );

create index if not exists publication_id
    on acte (publication_id);

create table if not exists pj_acte
(
    id             int auto_increment
    primary key,
    name           varchar(200) not null,
    url            varchar(500) not null,
    path           varchar(500) not null,
    publication_id int          null,
    hash           varchar(65)  null,
    constraint pj_acte_ibfk_1
    foreign key (publication_id) references publication (id)
    );

create index if not exists publication_id
    on pj_acte (publication_id);

create table if not exists publication_old
(
    id                    int auto_increment
    primary key,
    numero_de_lacte       varchar(20)            not null,
    objet                 varchar(256)           not null,
    siren                 varchar(9)             not null,
    publication_open_data varchar(1) default '0' not null,
    date_de_lacte         datetime               not null,
    classification_code   varchar(10)            not null,
    classification_nom    varchar(100)           not null,
    acte_nature           varchar(50)            not null,
    envoi_depot           varchar(50)            not null,
    date_budget           varchar(10)            null,
    est_masque            tinyint(1) default 0   not null,
    etat                  varchar(1) default '0' not null,
    created_at            datetime               not null,
    modified_at           datetime               not null,
    est_supprime          tinyint(1) default 0   null,
    date_publication      datetime               null
    );

create table if not exists task_id_sequence
(
    next_not_cached_value bigint(21)          not null,
    minimum_value         bigint(21)          not null,
    maximum_value         bigint(21)          not null,
    start_value           bigint(21)          not null comment 'start value when sequences is created or value if RESTART is used',
    increment             bigint(21)          not null comment 'increment value',
    cache_size            bigint(21) unsigned not null,
    cycle_option          tinyint(1) unsigned not null comment '0 if no cycles are allowed, 1 if the sequence should begin a new cycle when maximum_value is passed',
    cycle_count           bigint(21)          not null comment 'How many cycles have been done'
    );

create table if not exists taskset_id_sequence
(
    next_not_cached_value bigint(21)          not null,
    minimum_value         bigint(21)          not null,
    maximum_value         bigint(21)          not null,
    start_value           bigint(21)          not null comment 'start value when sequences is created or value if RESTART is used',
    increment             bigint(21)          not null comment 'increment value',
    cache_size            bigint(21) unsigned not null,
    cycle_option          tinyint(1) unsigned not null comment '0 if no cycles are allowed, 1 if the sequence should begin a new cycle when maximum_value is passed',
    cycle_count           bigint(21)          not null comment 'How many cycles have been done'
    );

