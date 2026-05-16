CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    marked_by BIGINT NOT NULL,
    remarks VARCHAR(255),
    created_at DATETIME,
    UNIQUE KEY uk_student_date (student_id, date),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_admin FOREIGN KEY (marked_by) REFERENCES admins(id)
);
