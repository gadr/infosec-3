# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table Grupo (
  gid                       integer not null,
  name                      varchar(255),
  constraint pk_Grupo primary key (gid))
;

create sequence Grupo_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists Grupo;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists Grupo_seq;

