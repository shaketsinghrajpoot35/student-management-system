-- Remove unique constraint to allow multiple users (Teachers) to share the same School Code
ALTER TABLE admins DROP INDEX school_code;
