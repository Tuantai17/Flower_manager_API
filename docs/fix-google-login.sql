-- Fix Google Login issue: Allow phone_number to be null for Google OAuth users
-- Run this SQL in phpMyAdmin or MySQL console

-- 1. Remove NOT NULL constraint from phone_number column
ALTER TABLE users MODIFY COLUMN phone_number VARCHAR(255) NULL;

-- 2. Verify the change
DESCRIBE users;
