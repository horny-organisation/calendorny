CREATE TYPE task_status_enum AS ENUM ('PENDING', 'COMPLETED', 'CANCELLED');

CREATE TABLE single_tasks
(
    id          UUID PRIMARY KEY,
    user_id     UUID,
    title       VARCHAR(255),
    description TEXT,
    due_date    DATE,
    status      task_status_enum,
    -----------------------------------------------------------
    CONSTRAINT tasks_user_id_nn CHECK (user_id IS NOT NULL),
    CONSTRAINT tasks_title_nn CHECK (title IS NOT NULL),
    CONSTRAINT tasks_due_date_nn CHECK ( due_date IS NOT NULL ),
    CONSTRAINT tasks_status_nn CHECK (status IS NOT NULL)
);

CREATE TABLE recurrence_tasks
(
    id          UUID PRIMARY KEY,
    user_id     UUID,
    title       VARCHAR(255),
    description TEXT,
    status      task_status_enum,
    rrule       VARCHAR(255),
    next_date   DATE,
    --------------------------------------------------------------------
    CONSTRAINT recurrence_tasks_user_id_nn CHECK (user_id IS NOT NULL),
    CONSTRAINT recurrence_tasks_title_nn CHECK (title IS NOT NULL),
    CONSTRAINT recurrence_tasks_status_nn CHECK (status IS NOT NULL),
    CONSTRAINT recurrence_tasks_rrule_nn CHECK (rrule IS NOT NULL),
    CONSTRAINT recurrence_tasks_next_date_nn CHECK (next_date IS NOT NULL)
);
