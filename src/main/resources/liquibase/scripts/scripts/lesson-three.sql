-- liquibase formatted sql

-- changeset apavlov:1
CREATE INDEX student_name_idx ON student (name);
CREATE INDEX faculty_nc_idx ON faculty (name, color);