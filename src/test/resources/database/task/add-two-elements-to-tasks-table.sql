insert into labels (id, name, color, is_deleted) values (1, 'red label', 'red',0);
insert into tasks (id, name, description, priority, deadline, status, project_id, assignee_id, is_deleted)
values (1, 'task', 'description', 'LOW', '2026-03-21',
        'IN_PROGRESS', 1, 2, 0);
insert into tasks (id, name, description, priority, deadline, status, project_id, assignee_id, is_deleted)
values (2, 'task', 'description', 'HIGH', '2026-03-21',
        'IN_PROGRESS', 2, 2, 0);
insert into tasks_labels (id, tasks_id, labels_id)
values (1,1,1);