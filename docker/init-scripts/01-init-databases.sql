-- MELI Order Manager - Database Initialization Script
-- This script creates additional databases for different environments

-- Create development database (if using separate DB)
-- CREATE DATABASE orderdb_dev
--     WITH ENCODING = 'UTF8'
--     LC_COLLATE = 'en_US.utf8'
--     LC_CTYPE = 'en_US.utf8'
--     TEMPLATE = template0;

-- Create testing database
CREATE DATABASE orderdb_test
    WITH ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TEMPLATE = template0;

-- Create production database (for local testing)
CREATE DATABASE orderdb_prod
    WITH ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TEMPLATE = template0;

-- Create application user for production (more secure)
-- CREATE USER orderapp WITH ENCRYPTED PASSWORD 'secure_password_here';

-- Grant permissions to the application user
-- GRANT CONNECT ON DATABASE orderdb_prod TO orderapp;
-- GRANT USAGE ON SCHEMA public TO orderapp;
-- GRANT CREATE ON SCHEMA public TO orderapp;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO orderapp;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO orderapp;

-- Set default privileges for future tables and sequences
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO orderapp;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO orderapp;