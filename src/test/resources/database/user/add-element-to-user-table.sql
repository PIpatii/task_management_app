insert into users (id, email, password, first_name, last_name, is_deleted)
values (2, 'email', 'password', 'firstName', 'lastName', 0);
insert into users_roles (id, users_id, roles_id) values (2, 2,2)