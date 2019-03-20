#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER docker;
    CREATE DATABASE docker;
    GRANT ALL PRIVILEGES ON DATABASE docker TO docker;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "docker" <<-EOSQL
    CREATE SEQUENCE users_id_seq;
    CREATE TABLE users (
        id INTEGER NOT NULL DEFAULT nextval('users_id_seq'),
        "name" VARCHAR NOT NULL,
        "timestamp" TIMESTAMP,
        PRIMARY KEY (id)
    );
    ALTER SEQUENCE users_id_seq OWNED BY users.id;

    CREATE SEQUENCE kafka_users_id_seq;
    CREATE TABLE kafka_users (
        id INTEGER NOT NULL DEFAULT nextval('kafka_users_id_seq'),
        "name" VARCHAR NOT NULL,
        "count" INTEGER,
        "startTime" TIMESTAMP,
        "endTime" TIMESTAMP,
        PRIMARY KEY (id)
    );
    ALTER SEQUENCE kafka_users_id_seq OWNED BY kafka_users.id;
EOSQL