ALTER TABLE student ADD CONSTRAINT age CHECK (age >= 16);

ALTER TABLE student
ADD CONSTRAINT unique_non_empty_name CHECK (name IS NOT NULL AND name <> ''),
ADD CONSTRAINT unique_name UNIQUE (name);

ALTER TABLE faculty
ADD CONSTRAINT unique_name_color UNIQUE (name, color);

ALTER TABLE student ALTER COLUMN age SET DEFAULT 20;