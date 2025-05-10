CREATE TABLE single_tasks
(
    id          UUID,
    user_id     UUID,
    title       VARCHAR,
    description TEXT,
    due_date    DATE,
    status      VARCHAR(30),
    ---------------------------------------------------------
    CONSTRAINT tasks_id_pk PRIMARY KEY (id),
    CONSTRAINT tasks_user_id_nn CHECK ( user_id IS NOT NULL ),
    CONSTRAINT tasks_title_nn CHECK ( title IS NOT NULL ),
    CONSTRAINT tasks_status_nn CHECK ( status IS NOT NULL )
);

CREATE TABLE recurrence_tasks
(
    id          UUID,
    user_id     UUID,
    title       VARCHAR,
    description TEXT,
    status      VARCHAR(30),
    rrule       VARCHAR,
    next_date   DATE,
    ---------------------------------------------------------
    CONSTRAINT recurrence_tasks_id_pk PRIMARY KEY (id),
    CONSTRAINT recurrence_tasks_user_id_nn CHECK ( user_id IS NOT NULL ),
    CONSTRAINT recurrence_tasks_title_nn CHECK ( title IS NOT NULL ),
    CONSTRAINT recurrence_tasks_status_nn CHECK ( status IS NOT NULL ),
    CONSTRAINT recurrence_tasks_rrule_nn CHECK ( rrule IS NOT NULL ),
    CONSTRAINT recurrence_tasks_next_date_nn CHECK ( next_date IS NOT NULL)
)
