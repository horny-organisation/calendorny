CREATE TABLE tasks
(
    id          UUID,
    user_id     UUID,
    title       VARCHAR,
    description TEXT,
    due_date    DATE,
    status      VARCHAR(30),
    rrule       VARCHAR,
    ---------------------------------------------------------
    CONSTRAINT tasks_id_pk PRIMARY KEY (id),
    CONSTRAINT tasks_user_id_nn CHECK ( user_id IS NOT NULL ),
    CONSTRAINT tasks_title_nn CHECK ( title IS NOT NULL ),
    CONSTRAINT tasks_status_nn CHECK ( status IS NOT NULL )
)
