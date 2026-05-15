-- Add is_approved column to admins table for teacher approval workflow
ALTER TABLE admins ADD COLUMN is_approved BOOLEAN NOT NULL DEFAULT TRUE;

-- Update existing teachers to be unapproved by default (optional, if any exist)
-- UPDATE admins SET is_approved = FALSE WHERE role = 'ROLE_TEACHER';
