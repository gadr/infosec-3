# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table Grupo (
  gid                       bigint not null,
  name                      varchar(255),
  constraint pk_Grupo primary key (gid))
;

create table Usuario (
  gid                       bigint not null,
  username                  varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  group_gid                 bigint,
  salt                      varchar(255),
  access_number             integer,
  blocked                   boolean,
  tries                     integer,
  blocked_since             timestamp,
  public_key_path           varchar(255),
  public_key                varbinary(255),
  constraint pk_Usuario primary key (gid))
;

create sequence Grupo_seq;

create sequence Usuario_seq;

alter table Usuario add constraint fk_Usuario_group_1 foreign key (group_gid) references Grupo (gid) on delete restrict on update restrict;
create index ix_Usuario_group_1 on Usuario (group_gid);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists Grupo;

drop table if exists Usuario;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists Grupo_seq;

drop sequence if exists Usuario_seq;

