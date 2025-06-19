CREATE INDEX idx_event_organizer ON event(organizer_id);
CREATE INDEX idx_event_time_range ON event(start_time, end_time);
CREATE INDEX idx_participant_user ON participants(user_id);
CREATE INDEX idx_reminder_user ON reminders(user_id);
