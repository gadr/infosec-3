# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table Grupo (
  gid                       bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_Grupo primary key (gid))
;

create table Usuario (
  gid                       bigint auto_increment not null,
  username                  varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  group_gid                 bigint,
  salt                      varchar(255),
  access_number             integer,
  blocked                   tinyint(1) default 0,
  tries                     integer,
  blocked_since             datetime,
  public_key_path           varchar(255),
  public_key                varbinary(255),
  constraint pk_Usuario primary key (gid))
;

alter table Usuario add constraint fk_Usuario_group_1 foreign key (group_gid) references Grupo (gid) on delete restrict on update restrict;
create index ix_Usuario_group_1 on Usuario (group_gid);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table Grupo;

drop table Usuario;

SET FOREIGN_KEY_CHECKS=1;

