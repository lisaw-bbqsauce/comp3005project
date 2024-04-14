DROP SCHEMA IF EXISTS comp3005project CASCADE;

CREATE SCHEMA comp3005project;

-- CREATE TABLES --
CREATE TABLE comp3005project.User (
    user_id SERIAL PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    email text UNIQUE NOT NULL,
    password text NOT NULL,
    phone_number text,
    address text,
    class_id INT,
    role text NOT NULL,
    subscription_start_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE comp3005project.Health_metric (
    metric_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    metric text UNIQUE,
    value text,
    date_recorded DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (member_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Fitness_goal (
    goal_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    run text,
    swim text,
    bike text,
    FOREIGN KEY (member_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Fitness_achievement (
    achievement_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    description text,
    date_achieved DATE,
    FOREIGN KEY (member_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Exercise_routine (
    routine_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    description text,
    duration TEXT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Trainer_schedule (
    schedule_id SERIAL PRIMARY KEY,
    trainer_id INT NOT NULL,
    session_id INT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Personal_session (
    session_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    trainer_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    duration INT NOT NULL,
    status text NOT NULL,
    FOREIGN KEY (member_id) REFERENCES comp3005project.User(user_id),
    FOREIGN KEY (trainer_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Group_class (
    class_id SERIAL PRIMARY KEY,
    name text NOT NULL,
    trainer_id INT NOT NULL,
    capacity INT,
    date DATE NOT NULL,
    duration INT NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Class_schedule (
    schedule_id SERIAL PRIMARY KEY,
    staff_id INT NOT NULL,
    class_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES comp3005project.User(user_id),
    FOREIGN KEY (class_id) REFERENCES comp3005project.Group_class(class_id)
);


CREATE TABLE comp3005project.Billing (
    billing_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    amount_due DECIMAL NOT NULL,
    date_generated DATE DEFAULT CURRENT_DATE,
    status text NOT NULL,
    FOREIGN KEY (member_id) REFERENCES comp3005project.User(user_id)
);

CREATE TABLE comp3005project.Equipment (
    equipment_id SERIAL PRIMARY KEY,
    name text UNIQUE NOT NULL,
    equipment_status text NOT NULL,
    last_maintenance DATE
);

CREATE TABLE comp3005project.Room (
    room_id SERIAL PRIMARY KEY,
    name text UNIQUE NOT NULL,
    status text NOT NULL
);

CREATE TABLE comp3005project.Room_booking (
    booking_id SERIAL PRIMARY KEY,
    staff_id INT NOT NULL,
    room_id INT NOT NULL,
    date DATE DEFAULT CURRENT_DATE,
    duration TEXT NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES comp3005project.User(user_id),
    FOREIGN KEY (room_id) REFERENCES comp3005project.Room(room_id)
);