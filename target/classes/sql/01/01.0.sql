DROP SCHEMA IF EXISTS comp3005project CASCADE;

CREATE SCHEMA comp3005project;

-- CREATE TABLES --
CREATE TABLE comp3005project.member (
    member_id SERIAL PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text UNIQUE NOT NULL,
    password text NOT NULL,
    phone_number text NOT NULL,
    address text NOT NULL,
    metric_id INT,
    subscription_start_date DATE NOT NULL,
    subscritption_end_date DATE,
    FOREIGN KEY (metric_id) REFERENCES comp3005project.metric(metric_id),
);

CREATE TABLE comp3005project.health_metric (
    metric_id SERIAL PRIMARY KEY,
    weight DECIMAL,
    height DECIMAL,
    date_recorded DATE
);

CREATE TABLE comp3005project.fitness_goal (
    goal_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    run text,
    swim text,
    bike text,
    FOREIGN KEY (member_id) REFERENCES comp3005project.members(member_id)
);

CREATE TABLE comp3005project.fitness_achievement (
    achievement_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    description text,
    date_achieved DATE,
    FOREIGN KEY (member_id) REFERENCES comp3005project.members(member_id)
);

CREATE TABLE comp3005project.exercice_routine (
    routine_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    description text,
    date_time DATETIME,
    duration INT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES comp3005project.members(member_id)
);

CREATE TABLE comp3005project.trainer (
    trainer_id SERIAL PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text UNIQUE NOT NULL,
    password text NOT NULL
);

CREATE TABLE comp3005project.trainer_schedule (
    schedule_id SERIAL PRIMARY KEY,
    trainer_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    duration INT NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES comp3005project.trainers(trainer_id)
);

CREATE TABLE comp3005project.admin_staff (
    staff_id SERIAL PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text UNIQUE NOT NULL,
    password text NOT NULL,
    role text NOT NULL
);

CREATE TABLE comp3005project.personal_session (
    session_id SERIAL PRIMARY KEY,
    name text NOT NULL,
    trainer_id INT NOT NULL,
    member_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    duration INT NOT NULL,
    session_status ENUM('scheduled', 'completed', 'cancelled'),
    FOREIGN KEY (member_id) REFERENCES comp3005project.members(member_id),
    FOREIGN KEY (trainer_id) REFERENCES comp3005project.trainers(trainer_id)
);

CREATE TABLE comp3005project.group_session (
    class_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    trainer_id INT NOT NULL,
    capacity INT NOT NULL,
    date_time DATETIME NOT NULL,
    duration INT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES comp3005project.members(member_id),
    FOREIGN KEY (trainer_id) REFERENCES comp3005project.trainers(trainer_id)
);

CREATE TABLE comp3005project.class_schedule (
    schedule_id SERIAL PRIMARY KEY,
    staff_id INT NOT NULL,
    class_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    duration INT NOT NULL,
    FOREIGN KEY (class_id) REFERENCES comp3005project.group_session(class_id),
    FOREIGN KEY (staff_id) REFERENCES comp3005project.admin_staff(staff_id)
);

CREATE TABLE comp3005project.billing (
    billing_id SERIAL PRIMARY KEY,
    staff_id INT NOT NULL,
    member_id INT NOT NULL,
    amount_due DECIMAL NOT NULL,
    date_generated DATE NOT NULL,
    status ENUM('pending', 'paid'),
    FOREIGN KEY (staff_id) REFERENCES comp3005project.admin_staff(staff_id),
    FOREIGN KEY (member_id) REFERENCES comp3005project.members(member_id)
);

CREATE TABLE comp3005project.payment (
    payment_id SERIAL PRIMARY KEY,
    billing_id INT NOT NULL,
    amount_paid DECIMAL NOT NULL,
    date_paid DATE NOT NULL,
    FOREIGN KEY (billing_id) REFERENCES comp3005project.billing(billing_id)
);

CREATE TABLE comp3005project.equipment (
    equipment_id SERIAL PRIMARY KEY,
    staff_id INT NOT NULL,
    name text NOT NULL,
    equipment_status ENUM('available', 'in_use', 'maintenance'),
    last_maintenance DATE,
    FOREIGN KEY (staff_id) REFERENCES comp3005project.admin_staff(staff_id)
);

CREATE TABLE comp3005project.room_booking (
    booking_id SERIAL PRIMARY KEY,
    staff_id INT NOT NULL,
    room_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    duration INT NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES comp3005project.admin_staff(staff_id)
);

-- INSERT DATA --