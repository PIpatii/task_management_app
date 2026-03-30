insert into users (id, email, password, first_name, last_name, is_deleted)
values (2, 'email', '$2a$10$ZTrIJiIql7dj0tA2Xdeb3Ox.AhxWSmPFmE00BuZbOnLBD4jKDd9Nm', 'firstName', 'lastName', 0);
insert into users_roles (id, users_id, roles_id) values (2, 2,2)