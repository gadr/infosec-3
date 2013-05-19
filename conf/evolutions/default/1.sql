# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table Grupo (
  gid                       bigint not null,
  name                      varchar(255),
  constraint pk_Grupo primary key (gid))
;

create table Usario (
  gid                       bigint not null,
  username                  varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  group_gid                 bigint,
  access_number             integer,
  blocked                   boolean,
  blocked_since             timestamp,
  constraint pk_Usario primary key (gid))
;

create sequence Grupo_seq;

create sequence Usario_seq;

alter table Usario add constraint fk_Usario_group_1 foreign key (group_gid) references Grupo (gid) on delete restrict on update restrict;
create index ix_Usario_group_1 on Usario (group_gid);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists Grupo;

drop table if exists Usario;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists Grupo_seq;

drop sequence if exists Usario_seq;

