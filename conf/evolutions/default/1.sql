# --- !Ups
create table "TEAM" (
  "ID" SERIAL NOT NULL PRIMARY KEY,
  "TEAMNAME" VARCHAR(254) NOT NULL UNIQUE,
  "MEMBERCOUNT" BIGINT,
  "URLPATH" VARCHAR(254),
  "CASH" BIGINT,
  "MASTERID" BIGINT NOT NULL);

create table "MEMBER" (
  "TEAMNAME" VARCHAR(254) REFERENCES "TEAM"("TEAMNAME"),
  "ID" SERIAL PRIMARY KEY,
  "NAME" VARCHAR(254) NOT NULL,
  "PASSWORD" VARCHAR(254) NOT NULL,
  "JOB" VARCHAR(254),
  "AGE" BIGINT
  );

# --- !Downs
drop table "TEAM";
drop table "MEMBER";