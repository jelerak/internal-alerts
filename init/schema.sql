CREATE USER internal_alerts WITH SUPERUSER PASSWORD 'internal_alerts';
CREATE DATABASE internal_alerts  WITH OWNER = internal_alerts TABLESPACE = pg_default;
GRANT TEMPORARY, CONNECT ON DATABASE internal_alerts TO PUBLIC;
\connect internal_alerts
CREATE SCHEMA internal_alerts AUTHORIZATION internal_alerts;
