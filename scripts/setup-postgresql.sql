-- PostgreSQL Database Setup Script for Order Manager
-- This script creates the required databases for all environments

-- Create Development Database
CREATE DATABASE orderdb_dev
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE orderdb_dev IS 'Order Manager Development Database';

-- Create Test Database
CREATE DATABASE orderdb_test
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE orderdb_test IS 'Order Manager Test Database';

-- Create Production Database
CREATE DATABASE orderdb_prod
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE orderdb_prod IS 'Order Manager Production Database';

-- Grant necessary privileges (run these for each database)
-- For development database
\c orderdb_dev
GRANT ALL PRIVILEGES ON DATABASE orderdb_dev TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;

-- For test database
\c orderdb_test
GRANT ALL PRIVILEGES ON DATABASE orderdb_test TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;

-- For production database
\c orderdb_prod
GRANT ALL PRIVILEGES ON DATABASE orderdb_prod TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;

-- Display created databases
\l