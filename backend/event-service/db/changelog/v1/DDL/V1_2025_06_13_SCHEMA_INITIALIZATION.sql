CREATE TABLE event_labels
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY,
    name  VARCHAR(50),
    color VARCHAR(7),
    ------------------------------------------------------
    CONSTRAINT ev_lab_id_pk PRIMARY KEY (id),
    CONSTRAINT ev_lab_name_nn CHECK ( name IS NOT NULL ),
    CONSTRAINT ev_lab_color_nn CHECK ( color IS NOT NULL )
);

CREATE TABLE reminder_methods
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50),
    -----------------------------------------------------
    CONSTRAINT ren_meth_id_pk PRIMARY KEY (id),
    CONSTRAINT rem_meth_name_nn CHECK ( name IS NOT NULL )
);

CREATE TABLE event
(
    id                UUID,
    title             VARCHAR(200),
    description       TEXT,
    location          VARCHAR(255),
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    rrule             VARCHAR(255),
    is_meeting        BOOLEAN DEFAULT FALSE,
    video_meeting_url VARCHAR(255),
    is_active         BOOLEAN,
    ------------------------------------------------------------
    CONSTRAINT ev_id_pk PRIMARY KEY (id),
    CONSTRAINT ev_title_nn CHECK ( title IS NOT NULL ),
    CONSTRAINT ev_start_time_nn CHECK ( start_time IS NOT NULL ),
    CONSTRAINT ev_end_time_nn CHECK ( end_time IS NOT NULL ),
    CONSTRAINT ev_is_active_nn CHECK ( is_active IS NOT NULL )
);

CREATE TABLE reminders
(
    id              UUID,
    event_id        UUID,
    minutes_before  INTEGER,
    reminder_method BIGINT,
    -----------------------------------------------------------------------------------------------
    CONSTRAINT rem_id_pk PRIMARY KEY (id),
    CONSTRAINT rem_event_id_nn CHECK ( event_id IS NOT NULL ),
    CONSTRAINT rem_event_id_fk FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT ren_minutes_before_nn CHECK ( minutes_before IS NOT NULL ),
    CONSTRAINT rem_reminder_method_nn CHECK ( reminder_method IS NOT NULL ),
    CONSTRAINT rem_reminder_method_fk FOREIGN KEY (reminder_method) REFERENCES reminder_methods (id)
);

CREATE TABLE event_label_links
(
    event_id UUID,
    label_id BIGINT,
    --------------------------------------------------------------------------------------
    CONSTRAINT ev_lab_link_pk PRIMARY KEY (event_id, label_id),
    CONSTRAINT ev_lab_link_event_id_nn CHECK ( event_id IS NOT NULL ),
    CONSTRAINT ev_lab_link_event_id_fk FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT ev_lab_link_label_id_nn CHECK ( label_id IS NOT NULL ),
    CONSTRAINT ev_lab_link_label_id_fk FOREIGN KEY (label_id) REFERENCES event_labels (id)
);

CREATE TABLE participants
(
    id           UUID,
    event_id     UUID,
    user_id      UUID,
    status       VARCHAR(30),
    invited_at   TIMESTAMP,
    responded_at TIMESTAMP,
    --------------------------------------------------------------------------------
    CONSTRAINT participant_id_pk PRIMARY KEY (id),
    CONSTRAINT participant_event_id_nn CHECK ( event_id IS NOT NULL ),
    CONSTRAINT participant_event_id_fk FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT participant_user_id_nn CHECK ( user_id IS NOT NULL ),
    CONSTRAINT participant_status_nn CHECK ( status IS NOT NULL ),
    CONSTRAINT participant_invited_at_nn CHECK ( invited_at IS NOT NULL ),
    CONSTRAINT participant_responded_at_nn CHECK ( responded_at IS NOT NULL )
);

CREATE TABLE organizers
(
    id       UUID,
    event_id UUID,
    user_id  UUID,
    -----------------------------------------------------------------------------
    CONSTRAINT organizer_id_pk PRIMARY KEY (id),
    CONSTRAINT organizer_event_id_nn CHECK ( event_id IS NOT NULL ),
    CONSTRAINT organizer_event_id_fk FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT organizer_user_id_nn CHECK ( user_id IS NOT NULL )
);
