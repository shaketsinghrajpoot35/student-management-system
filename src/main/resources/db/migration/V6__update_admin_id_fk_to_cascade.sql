-- Update foreign key to support cascade delete when a teacher is removed
ALTER TABLE students DROP FOREIGN KEY FKhq4yallbt7ntssrd7i6tmpo25;
ALTER TABLE students ADD CONSTRAINT FKhq4yallbt7ntssrd7i6tmpo25 
    FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE CASCADE;
