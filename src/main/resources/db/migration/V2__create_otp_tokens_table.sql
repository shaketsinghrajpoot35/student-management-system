-- V2: Create otp_tokens table for Forgot Password / OTP flow
-- and ensure school_name column exists on admins table (for sign-up feature)
CREATE TABLE IF NOT EXISTS otp_tokens (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    email       VARCHAR(100) NOT NULL,
    otp         VARCHAR(6)   NOT NULL,
    expiry_date DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_otp_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add school_name column to admins if it doesn't exist
-- (safe to run on existing DBs that may or may not have it)
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'admins' AND COLUMN_NAME = 'school_name');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE admins ADD COLUMN school_name VARCHAR(150) DEFAULT NULL',
    'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
