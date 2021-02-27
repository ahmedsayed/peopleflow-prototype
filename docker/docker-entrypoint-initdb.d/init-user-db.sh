#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER pplflw WITH PASSWORD 'pplflw';
    CREATE DATABASE pplflw;
    GRANT ALL PRIVILEGES ON DATABASE pplflw TO pplflw;
EOSQL