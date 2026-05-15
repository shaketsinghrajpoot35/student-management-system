ALTER TABLE admins ADD COLUMN school_code VARCHAR(20) UNIQUE;

-- Generate random school codes for existing admins to avoid null unique constraint issues
UPDATE admins SET school_code = UPPER(SUBSTRING(MD5(RAND()), 1, 8)) WHERE school_code IS NULL;
