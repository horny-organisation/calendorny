CREATE INDEX idx_single_tasks_status_due_date ON single_tasks (status, due_date);

CREATE INDEX idx_recurrence_tasks_status_next_date ON recurrence_tasks (status, next_date);
