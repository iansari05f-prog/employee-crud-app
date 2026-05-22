-- Only runs when an old employees table already exists in Neon

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'employees'
    ) THEN
        ALTER TABLE employees ADD COLUMN IF NOT EXISTS experience_years INTEGER;
        ALTER TABLE employees ADD COLUMN IF NOT EXISTS department_id BIGINT;
        ALTER TABLE employees ADD COLUMN IF NOT EXISTS photo_path VARCHAR(500);
        ALTER TABLE employees ADD COLUMN IF NOT EXISTS resume_path VARCHAR(500);

        UPDATE employees SET experience_years = 0 WHERE experience_years IS NULL;

        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'employees' AND column_name = 'department'
        ) THEN
            UPDATE employees e
            SET department_id = d.id
            FROM departments d
            WHERE e.department_id IS NULL AND LOWER(d.name) = LOWER(e.department);

            ALTER TABLE employees DROP COLUMN department;
        END IF;

        UPDATE employees
        SET department_id = (SELECT id FROM departments ORDER BY id LIMIT 1)
        WHERE department_id IS NULL;

        ALTER TABLE employees ALTER COLUMN experience_years SET NOT NULL;
        ALTER TABLE employees ALTER COLUMN department_id SET NOT NULL;

        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'fk_employees_department'
        ) THEN
            ALTER TABLE employees
                ADD CONSTRAINT fk_employees_department
                FOREIGN KEY (department_id) REFERENCES departments(id);
        END IF;
    END IF;
END $$;
