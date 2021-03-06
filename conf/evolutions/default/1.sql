# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table Grupo (
  gid                       bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_Grupo primary key (gid))
;

create table InfosecLog (
  gid                       bigint auto_increment not null,
  username                  varchar(255),
  filename                  varchar(255),
  date                      datetime,
  message_gid               bigint,
  constraint pk_InfosecLog primary key (gid))
;

create table Mensagem (
  gid                       bigint auto_increment not null,
  code                      varchar(255),
  message                   varchar(255),
  constraint pk_Mensagem primary key (gid))
;

create table Usuario (
  gid                       bigint auto_increment not null,
  username                  varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  group_gid                 bigint,
  salt                      varchar(255),
  access_number             integer,
  password_tries            integer,
  signature_tries           integer,
  blocked_until             datetime,
  public_key_path           varchar(255),
  public_key                varbinary(255),
  constraint pk_Usuario primary key (gid))
;

alter table InfosecLog add constraint fk_InfosecLog_message_1 foreign key (message_gid) references Mensagem (gid) on delete restrict on update restrict;
create index ix_InfosecLog_message_1 on InfosecLog (message_gid);
alter table Usuario add constraint fk_Usuario_group_2 foreign key (group_gid) references Grupo (gid) on delete restrict on update restrict;
create index ix_Usuario_group_2 on Usuario (group_gid);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table Grupo;

drop table InfosecLog;

drop table Mensagem;

drop table Usuario;

SET FOREIGN_KEY_CHECKS=1;

