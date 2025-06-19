import React from "react";
import { type CalendarMonth,
    type CalendarEvent,
    DAYS_OF_WEEK } from "../../../../entities/calendar";
import { Typography } from "../../../../shared";
import styles from "./MonthView.module.scss";

interface MonthViewProps {
    calendar: CalendarMonth;
    events: CalendarEvent[];
    onDateClick: (date: Date) => void;
    selectedDate: Date | null;
}

export const MonthView: React.FC<MonthViewProps> = ({
                                                        calendar,
                                                        events,
                                                        onDateClick,
                                                        selectedDate,
                                                    }) => {
    const getEventsForDate = (date: Date): CalendarEvent[] => {
        return events.filter((event) => {
            const eventDate = new Date(event.startDate);
            return (
                eventDate.getDate() === date.getDate() &&
                eventDate.getMonth() === date.getMonth() &&
                eventDate.getFullYear() === date.getFullYear()
            );
        });
    };

    return (
        <div className={styles.monthView}>
            {/* Header with days of week */}
            <div className={styles.daysHeader}>
                {DAYS_OF_WEEK.map((day) => (
                    <div key={day} className={styles.dayHeader}>
                        <Typography variant="caption">{day}</Typography>
                    </div>
                ))}
            </div>

            {/* Calendar grid */}
            <div className={styles.calendarGrid}>
                {calendar.weeks.map((week, weekIndex) => (
                    <div key={weekIndex} className={styles.week}>
                        {week.days.map((day, dayIndex) => {
                            const dayEvents = getEventsForDate(day.date);
                            const isSelected =
                                selectedDate && day.date.getTime() === selectedDate.getTime();

                            return (
                                <div
                                    key={dayIndex}
                                    className={`${styles.day} ${
                                        !day.isCurrentMonth ? styles.otherMonth : ""
                                    } ${day.isToday ? styles.today : ""} ${
                                        isSelected ? styles.selected : ""
                                    }`}
                                    onClick={() => onDateClick(day.date)}
                                >
                                    <div className={styles.dayNumber}>
                                        <Typography variant="body" className={styles.dayText}>
                                            {day.date.getDate()}
                                        </Typography>
                                    </div>

                                    <div className={styles.events}>
                                        {dayEvents.slice(0, 3).map((event) => (
                                            <div
                                                key={event.id}
                                                className={styles.event}
                                                style={{ backgroundColor: event.color }}
                                            >
                                                <Typography
                                                    variant="small"
                                                    className={styles.eventTitle}
                                                >
                                                    {event.title}
                                                </Typography>
                                            </div>
                                        ))}
                                        {dayEvents.length > 3 && (
                                            <div className={styles.moreEvents}>
                                                <Typography variant="small">
                                                    +{dayEvents.length - 3} еще
                                                </Typography>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                ))}
            </div>
        </div>
    );
};