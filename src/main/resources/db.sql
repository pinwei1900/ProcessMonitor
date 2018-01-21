create table commandTabel
(
  id INTEGER primary key
  autoincrement,
  ip TEXT not NULL,
  command TEXT not null,
  command_desc TEXT
)
;

create unique index commandTabel_command_uindex
  on commandTabel (command)
;

create table connectInfoTabel
(
  id INTEGER
    primary key
  autoincrement,
  ip TEXT not null,
  username TEXT not null,
  password TEXT not null,
  desc TEXT
)
;

create table prosessTable
(
  id INTEGER not null
    primary key
  autoincrement,
  ip TEXT not null,
  time DATETIME not null,
  value TEXT not null
)
;

create table systemInfoTabel
(
  id INTEGER
    primary key
  autoincrement,
  ip TEXT not null,
  time DATETIME not null,
  value TEXT not null
)
;




