#!/bin/sh
set -eu

create_database() {
  database="$1"
  echo "Creating database '${database}'"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres <<-EOSQL
    SELECT 'CREATE DATABASE ${database}'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${database}')\gexec
EOSQL
}

create_database product_db
create_database cart_db
create_database order_db
create_database payment_db
