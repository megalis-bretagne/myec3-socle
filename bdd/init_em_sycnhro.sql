create table if not exists QRTZ_GENERAL_CALENDARS
(
    CALENDAR_NAME varchar(200)                         not null,
    CALENDAR      blob                                 not null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_GENERAL_FIRED_TRIGGERS
(
    ENTRY_ID          varchar(95)                          not null,
    TRIGGER_NAME      varchar(200)                         not null,
    TRIGGER_GROUP     varchar(200)                         not null,
    INSTANCE_NAME     varchar(200)                         not null,
    FIRED_TIME        bigint(13)                           not null,
    PRIORITY          int                                  not null,
    STATE             varchar(16)                          not null,
    JOB_NAME          varchar(200)                         null,
    JOB_GROUP         varchar(200)                         null,
    REQUESTS_RECOVERY varchar(1)                           null,
    is_nonconcurrent  tinyint(1)                           null,
    is_update_data    tinyint(1)                           null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    SCHED_TIME        bigint                               not null,
    primary key (SCHED_NAME, ENTRY_ID)
    )
    charset = utf8;

create index if not exists idx_qrtz_general_ft_inst_job_req_rcvry
    on QRTZ_GENERAL_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);

create index if not exists idx_qrtz_general_ft_j_g
    on QRTZ_GENERAL_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_general_ft_jg
    on QRTZ_GENERAL_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_general_ft_t_g
    on QRTZ_GENERAL_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_general_ft_tg
    on QRTZ_GENERAL_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_general_ft_trig_inst_name
    on QRTZ_GENERAL_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);

create table if not exists QRTZ_GENERAL_JOB_DETAILS
(
    JOB_NAME          varchar(200)                         not null,
    JOB_GROUP         varchar(200)                         not null,
    DESCRIPTION       varchar(250)                         null,
    JOB_CLASS_NAME    varchar(250)                         not null,
    IS_DURABLE        varchar(1)                           not null,
    REQUESTS_RECOVERY varchar(1)                           not null,
    JOB_DATA          blob                                 null,
    is_nonconcurrent  tinyint(1)                           null,
    is_update_data    tinyint(1)                           null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
    )
    charset = utf8;

create index if not exists idx_qrtz_general_j_grp
    on QRTZ_GENERAL_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_general_j_req_recovery
    on QRTZ_GENERAL_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);

create table if not exists QRTZ_GENERAL_LOCKS
(
    LOCK_NAME  varchar(40)                          not null,
    SCHED_NAME varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, LOCK_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_GENERAL_PAUSED_TRIGGER_GRPS
(
    TRIGGER_GROUP varchar(200)                         not null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create table if not exists QRTZ_GENERAL_SCHEDULER_STATE
(
    INSTANCE_NAME     varchar(200)                         not null,
    LAST_CHECKIN_TIME bigint(13)                           not null,
    CHECKIN_INTERVAL  bigint(13)                           not null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_GENERAL_TRIGGERS
(
    TRIGGER_NAME   varchar(200)                         not null,
    TRIGGER_GROUP  varchar(200)                         not null,
    JOB_NAME       varchar(200)                         not null,
    JOB_GROUP      varchar(200)                         not null,
    DESCRIPTION    varchar(250)                         null,
    NEXT_FIRE_TIME bigint(13)                           null,
    PREV_FIRE_TIME bigint(13)                           null,
    PRIORITY       int                                  null,
    TRIGGER_STATE  varchar(16)                          not null,
    TRIGGER_TYPE   varchar(8)                           not null,
    START_TIME     bigint(13)                           not null,
    END_TIME       bigint(13)                           null,
    CALENDAR_NAME  varchar(200)                         null,
    MISFIRE_INSTR  smallint(2)                          null,
    JOB_DATA       blob                                 null,
    SCHED_NAME     varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_GENERAL_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references QRTZ_GENERAL_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
    )
    charset = utf8;

create table if not exists QRTZ_GENERAL_BLOB_TRIGGERS
(
    TRIGGER_NAME  varchar(200)                         not null,
    TRIGGER_GROUP varchar(200)                         not null,
    BLOB_DATA     blob                                 null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_GENERAL_BLOB_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_GENERAL_BLOB_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_GENERAL_CRON_TRIGGERS
(
    TRIGGER_NAME    varchar(200)                         not null,
    TRIGGER_GROUP   varchar(200)                         not null,
    CRON_EXPRESSION varchar(120)                         not null,
    TIME_ZONE_ID    varchar(80)                          null,
    SCHED_NAME      varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_GENERAL_CRON_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_GENERAL_CRON_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_GENERAL_SIMPLE_TRIGGERS
(
    TRIGGER_NAME    varchar(200)                         not null,
    TRIGGER_GROUP   varchar(200)                         not null,
    REPEAT_COUNT    bigint(7)                            not null,
    REPEAT_INTERVAL bigint(12)                           not null,
    TIMES_TRIGGERED bigint(10)                           not null,
    SCHED_NAME      varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_GENERAL_SIMPLE_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_GENERAL_SIMPLE_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_GENERAL_SIMPROP_TRIGGERS
(
    SCHED_NAME    varchar(120)   not null,
    TRIGGER_NAME  varchar(200)   not null,
    TRIGGER_GROUP varchar(200)   not null,
    STR_PROP_1    varchar(512)   null,
    STR_PROP_2    varchar(512)   null,
    STR_PROP_3    varchar(512)   null,
    INT_PROP_1    int            null,
    INT_PROP_2    int            null,
    LONG_PROP_1   bigint         null,
    LONG_PROP_2   bigint         null,
    DEC_PROP_1    decimal(13, 4) null,
    DEC_PROP_2    decimal(13, 4) null,
    BOOL_PROP_1   tinyint(1)     null,
    BOOL_PROP_2   tinyint(1)     null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_GENERAL_SIMPROP_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists JOB_NAME
    on QRTZ_GENERAL_TRIGGERS (JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_general_t_c
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, CALENDAR_NAME);

create index if not exists idx_qrtz_general_t_g
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_general_t_j
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_general_t_jg
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_general_t_n_g_state
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_general_t_n_state
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_general_t_next_fire_time
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_general_t_nft_misfire
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_general_t_nft_st
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_general_t_nft_st_misfire
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);

create index if not exists idx_qrtz_general_t_nft_st_misfire_grp
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_general_t_state
    on QRTZ_GENERAL_TRIGGERS (SCHED_NAME, TRIGGER_STATE);

create table if not exists QRTZ_JMS_CALENDARS
(
    CALENDAR_NAME varchar(200)                         not null,
    CALENDAR      blob                                 not null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_JMS_FIRED_TRIGGERS
(
    ENTRY_ID          varchar(95)                          not null,
    TRIGGER_NAME      varchar(200)                         not null,
    TRIGGER_GROUP     varchar(200)                         not null,
    INSTANCE_NAME     varchar(200)                         not null,
    FIRED_TIME        bigint(13)                           not null,
    PRIORITY          int                                  not null,
    STATE             varchar(16)                          not null,
    JOB_NAME          varchar(200)                         null,
    JOB_GROUP         varchar(200)                         null,
    REQUESTS_RECOVERY varchar(1)                           null,
    is_nonconcurrent  tinyint(1)                           null,
    is_update_data    tinyint(1)                           null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    SCHED_TIME        bigint                               not null,
    primary key (SCHED_NAME, ENTRY_ID)
    )
    charset = utf8;

create index if not exists idx_qrtz_jms_ft_inst_job_req_rcvry
    on QRTZ_JMS_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);

create index if not exists idx_qrtz_jms_ft_j_g
    on QRTZ_JMS_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_jms_ft_jg
    on QRTZ_JMS_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_jms_ft_t_g
    on QRTZ_JMS_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_jms_ft_tg
    on QRTZ_JMS_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_jms_ft_trig_inst_name
    on QRTZ_JMS_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);

create table if not exists QRTZ_JMS_JOB_DETAILS
(
    JOB_NAME          varchar(200)                         not null,
    JOB_GROUP         varchar(200)                         not null,
    DESCRIPTION       varchar(250)                         null,
    JOB_CLASS_NAME    varchar(250)                         not null,
    IS_DURABLE        varchar(1)                           not null,
    REQUESTS_RECOVERY varchar(1)                           not null,
    JOB_DATA          blob                                 null,
    is_nonconcurrent  tinyint(1)                           null,
    is_update_data    tinyint(1)                           null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
    )
    charset = utf8;

create index if not exists idx_qrtz_jms_j_grp
    on QRTZ_JMS_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_jms_j_req_recovery
    on QRTZ_JMS_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);

create table if not exists QRTZ_JMS_LOCKS
(
    LOCK_NAME  varchar(40)                          not null,
    SCHED_NAME varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, LOCK_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_JMS_PAUSED_TRIGGER_GRPS
(
    TRIGGER_GROUP varchar(200)                         not null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create table if not exists QRTZ_JMS_SCHEDULER_STATE
(
    INSTANCE_NAME     varchar(200)                         not null,
    LAST_CHECKIN_TIME bigint(13)                           not null,
    CHECKIN_INTERVAL  bigint(13)                           not null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_JMS_TRIGGERS
(
    TRIGGER_NAME   varchar(200)                         not null,
    TRIGGER_GROUP  varchar(200)                         not null,
    JOB_NAME       varchar(200)                         not null,
    JOB_GROUP      varchar(200)                         not null,
    DESCRIPTION    varchar(250)                         null,
    NEXT_FIRE_TIME bigint(13)                           null,
    PREV_FIRE_TIME bigint(13)                           null,
    PRIORITY       int                                  null,
    TRIGGER_STATE  varchar(16)                          not null,
    TRIGGER_TYPE   varchar(8)                           not null,
    START_TIME     bigint(13)                           not null,
    END_TIME       bigint(13)                           null,
    CALENDAR_NAME  varchar(200)                         null,
    MISFIRE_INSTR  smallint(2)                          null,
    JOB_DATA       blob                                 null,
    SCHED_NAME     varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_JMS_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references QRTZ_JMS_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
    )
    charset = utf8;

create table if not exists QRTZ_JMS_BLOB_TRIGGERS
(
    TRIGGER_NAME  varchar(200)                         not null,
    TRIGGER_GROUP varchar(200)                         not null,
    BLOB_DATA     blob                                 null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_JMS_BLOB_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_JMS_BLOB_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_JMS_CRON_TRIGGERS
(
    TRIGGER_NAME    varchar(200)                         not null,
    TRIGGER_GROUP   varchar(200)                         not null,
    CRON_EXPRESSION varchar(120)                         not null,
    TIME_ZONE_ID    varchar(80)                          null,
    SCHED_NAME      varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_JMS_CRON_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_JMS_CRON_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_JMS_SIMPLE_TRIGGERS
(
    TRIGGER_NAME    varchar(200)                         not null,
    TRIGGER_GROUP   varchar(200)                         not null,
    REPEAT_COUNT    bigint(7)                            not null,
    REPEAT_INTERVAL bigint(12)                           not null,
    TIMES_TRIGGERED bigint(10)                           not null,
    SCHED_NAME      varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_JMS_SIMPLE_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_JMS_SIMPLE_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_JMS_SIMPROP_TRIGGERS
(
    SCHED_NAME    varchar(120)   not null,
    TRIGGER_NAME  varchar(200)   not null,
    TRIGGER_GROUP varchar(200)   not null,
    STR_PROP_1    varchar(512)   null,
    STR_PROP_2    varchar(512)   null,
    STR_PROP_3    varchar(512)   null,
    INT_PROP_1    int            null,
    INT_PROP_2    int            null,
    LONG_PROP_1   bigint         null,
    LONG_PROP_2   bigint         null,
    DEC_PROP_1    decimal(13, 4) null,
    DEC_PROP_2    decimal(13, 4) null,
    BOOL_PROP_1   tinyint(1)     null,
    BOOL_PROP_2   tinyint(1)     null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_JMS_SIMPROP_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists JOB_NAME
    on QRTZ_JMS_TRIGGERS (JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_jms_t_c
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, CALENDAR_NAME);

create index if not exists idx_qrtz_jms_t_g
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_jms_t_j
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_jms_t_jg
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_jms_t_n_g_state
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_jms_t_n_state
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_jms_t_next_fire_time
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_jms_t_nft_misfire
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_jms_t_nft_st
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_jms_t_nft_st_misfire
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);

create index if not exists idx_qrtz_jms_t_nft_st_misfire_grp
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_jms_t_state
    on QRTZ_JMS_TRIGGERS (SCHED_NAME, TRIGGER_STATE);

create table if not exists QRTZ_PARALLEL_CALENDARS
(
    CALENDAR_NAME varchar(200)                         not null,
    CALENDAR      blob                                 not null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, CALENDAR_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_PARALLEL_FIRED_TRIGGERS
(
    ENTRY_ID          varchar(95)                          not null,
    TRIGGER_NAME      varchar(200)                         not null,
    TRIGGER_GROUP     varchar(200)                         not null,
    INSTANCE_NAME     varchar(200)                         not null,
    FIRED_TIME        bigint(13)                           not null,
    PRIORITY          int                                  not null,
    STATE             varchar(16)                          not null,
    JOB_NAME          varchar(200)                         null,
    JOB_GROUP         varchar(200)                         null,
    REQUESTS_RECOVERY varchar(1)                           null,
    is_nonconcurrent  tinyint(1)                           null,
    is_update_data    tinyint(1)                           null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    SCHED_TIME        bigint                               not null,
    primary key (SCHED_NAME, ENTRY_ID)
    )
    charset = utf8;

create index if not exists idx_qrtz_parallel_ft_inst_job_req_rcvry
    on QRTZ_PARALLEL_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);

create index if not exists idx_qrtz_parallel_ft_j_g
    on QRTZ_PARALLEL_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_parallel_ft_jg
    on QRTZ_PARALLEL_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_parallel_ft_t_g
    on QRTZ_PARALLEL_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_parallel_ft_tg
    on QRTZ_PARALLEL_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_parallel_ft_trig_inst_name
    on QRTZ_PARALLEL_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);

create table if not exists QRTZ_PARALLEL_JOB_DETAILS
(
    JOB_NAME          varchar(200)                         not null,
    JOB_GROUP         varchar(200)                         not null,
    DESCRIPTION       varchar(250)                         null,
    JOB_CLASS_NAME    varchar(250)                         not null,
    IS_DURABLE        varchar(1)                           not null,
    REQUESTS_RECOVERY varchar(1)                           not null,
    JOB_DATA          blob                                 null,
    is_nonconcurrent  tinyint(1)                           null,
    is_update_data    tinyint(1)                           null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
    )
    charset = utf8;

create index if not exists idx_qrtz_parallel_j_grp
    on QRTZ_PARALLEL_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_parallel_j_req_recovery
    on QRTZ_PARALLEL_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);

create table if not exists QRTZ_PARALLEL_LOCKS
(
    LOCK_NAME  varchar(40)                          not null,
    SCHED_NAME varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, LOCK_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_PARALLEL_PAUSED_TRIGGER_GRPS
(
    TRIGGER_GROUP varchar(200)                         not null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create table if not exists QRTZ_PARALLEL_SCHEDULER_STATE
(
    INSTANCE_NAME     varchar(200)                         not null,
    LAST_CHECKIN_TIME bigint(13)                           not null,
    CHECKIN_INTERVAL  bigint(13)                           not null,
    SCHED_NAME        varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, INSTANCE_NAME)
    )
    charset = utf8;

create table if not exists QRTZ_PARALLEL_TRIGGERS
(
    TRIGGER_NAME   varchar(200)                         not null,
    TRIGGER_GROUP  varchar(200)                         not null,
    JOB_NAME       varchar(200)                         not null,
    JOB_GROUP      varchar(200)                         not null,
    DESCRIPTION    varchar(250)                         null,
    NEXT_FIRE_TIME bigint(13)                           null,
    PREV_FIRE_TIME bigint(13)                           null,
    PRIORITY       int                                  null,
    TRIGGER_STATE  varchar(16)                          not null,
    TRIGGER_TYPE   varchar(8)                           not null,
    START_TIME     bigint(13)                           not null,
    END_TIME       bigint(13)                           null,
    CALENDAR_NAME  varchar(200)                         null,
    MISFIRE_INSTR  smallint(2)                          null,
    JOB_DATA       blob                                 null,
    SCHED_NAME     varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_PARALLEL_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP) references QRTZ_PARALLEL_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
    )
    charset = utf8;

create table if not exists QRTZ_PARALLEL_BLOB_TRIGGERS
(
    TRIGGER_NAME  varchar(200)                         not null,
    TRIGGER_GROUP varchar(200)                         not null,
    BLOB_DATA     blob                                 null,
    SCHED_NAME    varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_PARALLEL_BLOB_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_PARALLEL_BLOB_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_PARALLEL_CRON_TRIGGERS
(
    TRIGGER_NAME    varchar(200)                         not null,
    TRIGGER_GROUP   varchar(200)                         not null,
    CRON_EXPRESSION varchar(120)                         not null,
    TIME_ZONE_ID    varchar(80)                          null,
    SCHED_NAME      varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_PARALLEL_CRON_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_PARALLEL_CRON_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_PARALLEL_SIMPLE_TRIGGERS
(
    TRIGGER_NAME    varchar(200)                         not null,
    TRIGGER_GROUP   varchar(200)                         not null,
    REPEAT_COUNT    bigint(7)                            not null,
    REPEAT_INTERVAL bigint(12)                           not null,
    TIMES_TRIGGERED bigint(10)                           not null,
    SCHED_NAME      varchar(120) default 'TestScheduler' not null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_PARALLEL_SIMPLE_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists TRIGGER_NAME
    on QRTZ_PARALLEL_SIMPLE_TRIGGERS (TRIGGER_NAME, TRIGGER_GROUP);

create table if not exists QRTZ_PARALLEL_SIMPROP_TRIGGERS
(
    SCHED_NAME    varchar(120)   not null,
    TRIGGER_NAME  varchar(200)   not null,
    TRIGGER_GROUP varchar(200)   not null,
    STR_PROP_1    varchar(512)   null,
    STR_PROP_2    varchar(512)   null,
    STR_PROP_3    varchar(512)   null,
    INT_PROP_1    int            null,
    INT_PROP_2    int            null,
    LONG_PROP_1   bigint         null,
    LONG_PROP_2   bigint         null,
    DEC_PROP_1    decimal(13, 4) null,
    DEC_PROP_2    decimal(13, 4) null,
    BOOL_PROP_1   tinyint(1)     null,
    BOOL_PROP_2   tinyint(1)     null,
    primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    constraint QRTZ_PARALLEL_SIMPROP_TRIGGERS_ibfk_1
    foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP) references QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
    )
    charset = utf8;

create index if not exists JOB_NAME
    on QRTZ_PARALLEL_TRIGGERS (JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_parallel_t_c
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, CALENDAR_NAME);

create index if not exists idx_qrtz_parallel_t_g
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

create index if not exists idx_qrtz_parallel_t_j
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);

create index if not exists idx_qrtz_parallel_t_jg
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, JOB_GROUP);

create index if not exists idx_qrtz_parallel_t_n_g_state
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_parallel_t_n_state
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_parallel_t_next_fire_time
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_parallel_t_nft_misfire
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_parallel_t_nft_st
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);

create index if not exists idx_qrtz_parallel_t_nft_st_misfire
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);

create index if not exists idx_qrtz_parallel_t_nft_st_misfire_grp
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP, TRIGGER_STATE);

create index if not exists idx_qrtz_parallel_t_state
    on QRTZ_PARALLEL_TRIGGERS (SCHED_NAME, TRIGGER_STATE);

