-- INSERT DATA --
INSERT INTO comp3005project.User (first_name, last_name, email, password, phone_number, address, role)
VALUES ('John', 'Doe', 'john.doe@email.com', 'password', '123-456-7890', '1234 Main St', 'MEMBER');

INSERT INTO comp3005project.Health_metric (member_id, metric, value)
VALUES (1, 'WEIGHT', '200lbs');

INSERT INTO comp3005project.Fitness_goal (member_id, run, swim, bike)
VALUES (1, '5km', '1km', '10km');

INSERT INTO comp3005project.Fitness_achievement (member_id, description, date_achieved)
VALUES (1, 'Ran 5km', '2024-03-10');

INSERT INTO comp3005project.Exercise_routine (member_id, description, duration)
VALUES (1, 'Run 5km', '30 mins');

INSERT INTO comp3005project.Personal_session (member_id, trainer_id, start_time, duration, status)
VALUES (1, 2, '2024-04-12 09:00:00', 60, 'COMPLETED');

INSERT INTO comp3005project.Group_class (name, trainer_id, capacity, date, duration)
VALUES ('Yoga', 2, 10, '2024-04-12', 60);

INSERT INTO comp3005project.User (first_name, last_name, email, password, role)
VALUES ('Alice', 'Smith', 'alice@email.com', 'password', 'TRAINER');

INSERT INTO comp3005project.Trainer_schedule (trainer_id, start_time, end_time)
VALUES 
    (2, '2024-04-12 09:00:00', '2024-04-12 13:00:00'),
    (2, '2024-04-13 08:00:00', '2024-04-13 12:00:00');

INSERT INTO comp3005project.User (first_name, last_name, email, password, role)
VALUES ('Admin', 'Adminson', 'admin@email.com', 'adminpassword', 'ADMIN');

INSERT INTO comp3005project.Equipment (name, equipment_status, last_maintenance)
VALUES ('Treadmill', 'AVAILABLE', '2021-01-10');

INSERT INTO comp3005project.Equipment (name, equipment_status, last_maintenance)
VALUES ('Elliptical', 'AVAILABLE', '2023-10-10');

INSERT INTO comp3005project.Equipment (name, equipment_status, last_maintenance)
VALUES ('Rower', 'AVAILABLE', '2024-05-10');

INSERT INTO comp3005project.Room (name, status)
VALUES ('Gym 1', 'AVAILABLE');

INSERT INTO comp3005project.Room (name, status)
VALUES ('Gym 2', 'AVAILABLE');

INSERT INTO comp3005project.Room (name, status)
VALUES ('Massage room 1', 'AVAILABLE');

INSERT INTO comp3005project.Room_booking (staff_id, room_id, duration)
VALUES (3, 1, '1 hour');

