CREATE TABLE IF NOT EXISTS departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_departments_name UNIQUE (name)
);

INSERT INTO departments (name, description, created_at, updated_at)
SELECT 'IT', 'Information Technology', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE LOWER(name) = 'it');

INSERT INTO departments (name, description, created_at, updated_at)
SELECT 'HR', 'Human Resources', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE LOWER(name) = 'hr');

INSERT INTO departments (name, description, created_at, updated_at)
SELECT 'Finance', 'Finance Department', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE LOWER(name) = 'finance');

INSERT INTO departments (name, description, created_at, updated_at)
SELECT 'DevOps', 'DevOps and Infrastructure', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE LOWER(name) = 'devops');
