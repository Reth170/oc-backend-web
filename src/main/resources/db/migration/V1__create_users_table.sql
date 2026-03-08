-- Database Migration Script for Authentication System
-- Run this script to create the users table and related indexes

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    company VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_locked_until ON users(locked_until) WHERE locked_until IS NOT NULL;

-- Add constraint to ensure failed_login_attempts is non-negative
ALTER TABLE users ADD CONSTRAINT chk_failed_login_attempts
    CHECK (failed_login_attempts >= 0);

-- Comments for documentation
COMMENT ON TABLE users IS 'User accounts for authentication system';
COMMENT ON COLUMN users.id IS 'Primary key - UUID format';
COMMENT ON COLUMN users.username IS 'Unique username (3-20 characters, alphanumeric)';
COMMENT ON COLUMN users.email IS 'Unique email address';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.company IS 'Optional company name';
COMMENT ON COLUMN users.is_active IS 'Account active status';
COMMENT ON COLUMN users.failed_login_attempts IS 'Number of failed login attempts';
COMMENT ON COLUMN users.locked_until IS 'Account lock expiration time (NULL if not locked)';
COMMENT ON COLUMN users.created_at IS 'Account creation timestamp';
COMMENT ON COLUMN users.updated_at IS 'Last update timestamp';
