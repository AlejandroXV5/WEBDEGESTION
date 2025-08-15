-- Crear usuario y base de datos
CREATE USER pms_user WITH PASSWORD 'admin';
CREATE DATABASE pmsdb OWNER pms_user;
GRANT ALL PRIVILEGES ON DATABASE pmsdb TO pms_user;
