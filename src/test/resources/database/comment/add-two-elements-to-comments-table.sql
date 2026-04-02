insert into users (id, email, password, first_name, last_name, is_deleted)
values (2, 'email', 'password', 'firstName', 'lastName', 0);
insert into comments (id, task_id, user_id, text, timestamp)
values (1, 1, 2, 'text', '2026-03-21');
insert into comments (id, task_id, user_id, text, timestamp)
values (2, 1, 2, 'text', '2026-03-20');