-- Update subjects foreign key to support cascade delete when a teacher is removed
ALTER TABLE subjects DROP FOREIGN KEY FKs4ibljv95rseeohll3nj0mjs4;
ALTER TABLE subjects ADD CONSTRAINT FKs4ibljv95rseeohll3nj0mjs4 
    FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE CASCADE;

-- Update attendance foreign key to support cascade delete when a teacher is removed
ALTER TABLE attendance DROP FOREIGN KEY fk_attendance_admin;
ALTER TABLE attendance ADD CONSTRAINT fk_attendance_admin 
    FOREIGN KEY (marked_by) REFERENCES admins(id) ON DELETE CASCADE;
