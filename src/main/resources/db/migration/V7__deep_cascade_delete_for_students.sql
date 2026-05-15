-- Update child tables of students to support cascade delete
-- This allows removing a teacher to successfully delete their students and all related student data

-- 1. Academic Details
ALTER TABLE academic_details DROP FOREIGN KEY FKjjl6eiyor2xegf85uxvugudeh;
ALTER TABLE academic_details ADD CONSTRAINT FKjjl6eiyor2xegf85uxvugudeh 
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE;

-- 2. Bank Details
ALTER TABLE bank_details DROP FOREIGN KEY FK1t1o2v4sghye2r212nn4ew6m;
ALTER TABLE bank_details ADD CONSTRAINT FK1t1o2v4sghye2r212nn4ew6m 
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE;

-- 3. Student Documents
ALTER TABLE student_documents DROP FOREIGN KEY FK92xliv5g142xgioi3dxwm9sld;
ALTER TABLE student_documents ADD CONSTRAINT FK92xliv5g142xgioi3dxwm9sld 
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE;

-- 4. Student Subjects (Join Table)
ALTER TABLE student_subjects DROP FOREIGN KEY FKjb6x19uwrg0tewtrgv2o7ec2r;
ALTER TABLE student_subjects ADD CONSTRAINT FKjb6x19uwrg0tewtrgv2o7ec2r 
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE;
