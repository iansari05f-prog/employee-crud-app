CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    job_title VARCHAR(100) NOT NULL,
    department_id BIGINT NOT NULL,
    salary NUMERIC(12, 2) NOT NULL,
    experience_years INTEGER NOT NULL DEFAULT 0,
    photo_path VARCHAR(500),
    resume_path VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_employees_email UNIQUE (email),
    CONSTRAINT fk_employees_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    employee_id BIGINT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS attendance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time TIMESTAMP NOT NULL,
    check_out_time TIMESTAMP,
    working_hours DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    review_comment VARCHAR(500),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_leave_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS payrolls (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    base_salary NUMERIC(12, 2) NOT NULL,
    bonus NUMERIC(12, 2) NOT NULL DEFAULT 0,
    deductions NUMERIC(12, 2) NOT NULL DEFAULT 0,
    net_salary NUMERIC(12, 2) NOT NULL,
    pay_period_month INTEGER NOT NULL,
    pay_period_year INTEGER NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_payroll_employee_period UNIQUE (employee_id, pay_period_month, pay_period_year),
    CONSTRAINT fk_payroll_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS employee_documents (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    document_type VARCHAR(20) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    stored_path VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_document_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);
