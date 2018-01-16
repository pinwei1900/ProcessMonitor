create table commandTabel
(
  id INTEGER
    primary key
  autoincrement,
  command TEXT not null,
  command_desc TEXT
)
;

create unique index commandTabel_command_uindex
  on commandTabel (command)
;

create table prosessTable
(
  id INTEGER not null
    primary key
  autoincrement,
  time DATETIME not null,
  value TEXT not null
)
;

create table systemInfoTabel
(
  id INTEGER
    primary key
  autoincrement,
  time DATETIME not null,
  value TEXT not null
)
;

