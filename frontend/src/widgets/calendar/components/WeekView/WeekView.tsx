import React from "react";
import { type CalendarWeek,
    type CalendarEvent,
    DAYS_OF_WEEK } from "../../../../entities/calendar";
import { Typography } from "../../../../shared";
import styles from "./WeekView.module.scss";

interface WeekViewProps {
    week: CalendarWeek;
    events: CalendarEvent[];
    onDateClick: (date: Date) => void;
    onTimeSlotClick?: (date: Date, hour: number) => void;
    onDayHeaderClick?: (date: Date) => void;
    selectedDate: Date | null;
}

export const WeekView: React.FC<WeekViewProps> = ({
                                                      week,
                                                      events,
                                                      onDateClick,
                                                      onTimeSlotClick,
                                                      onDayHeaderClick,
                                                      selectedDate,
                                                  }) => {
    const timeSlots = Array.from({ length: 24 }, (_, i) => i);

    const getEventsForDateAndHour = (
        date: Date,
        hour: number,
    ): CalendarEvent[] => {
        return events.filter((event) => {
            const eventStart = new Date(event.startDate);
            const eventEnd = new Date(event.endDate);

            return (
                event.type === "event" && // Только мероприятия, не задачи
                eventStart.getDate() === date.getDate() &&
                eventStart.getMonth() === date.getMonth() &&
                eventStart.getFullYear() === date.getFullYear() &&
                eventStart.getHours() <= hour &&
                eventEnd.getHours() > hour
            );
        });
    };

    const getTasksForDate = (date: Date): CalendarEvent[] => {
        return events.filter((event) => {
            return (
                event.type === "task" &&
                event.startDate.getDate() === date.getDate() &&
                event.startDate.getMonth() === date.getMonth() &&
                event.startDate.getFullYear() === date.getFullYear()
            );
        });
    };

    const formatHour = (hour: number): string => {
        return `${hour.toString().padStart(2, "0")}:00`;
    };

    return (
        <div className={styles.weekView}>
            {/* Header with days */}
            <div className={styles.weekHeader}>
                <div className={styles.timeColumn}></div>
                {week.days.map((day, index) => {
                    const isSelected =
                        selectedDate && day.date.getTime() === selectedDate.getTime();
                    const dayTasks = getTasksForDate(day.date);

                    return (
                        <div
                            key={index}
                            className={`${styles.dayColumn} ${
                                day.isToday ? styles.today : ""
                            } ${isSelected ? styles.selected : ""}`}
                            onClick={() => onDateClick(day.date)}
                        >
                            <div
                                    className={styles.dayHeader}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        onDayHeaderClick?.(day.date);
                                    }}
                            >
                                <div className={styles.dayHeaderTop}>
                                    <Typography variant="caption" className={styles.dayName}>
                                        {DAYS_OF_WEEK[index]}
                                    </Typography>
                                    <div
                                        className={`${styles.dayNumber} ${
                                            day.isToday ? styles.todayNumber : ""
                                        }`}
                                    >
                                        <Typography variant="body">{day.date.getDate()}</Typography>
                                    </div>
                                </div>
                                {/* Задачи под заголовком дня */}
                                {dayTasks.length > 0 && (
                                    <div className={styles.tasksSection}>
                                        {dayTasks.map((task) => (
                                            <div
                                                key={task.id}
                                                className={styles.taskItem}
                                                style={{ backgroundColor: task.color }}
                                                onClick={(e) => e.stopPropagation()}
                                            >
                                                <Typography
                                                    variant="small"
                                                    className={styles.taskTitle}
                                                >
                                                    {task.title}
                                                </Typography>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </div>
                    )
                        ;
                })}
            </div>

            {/* Time grid */}
            <div className={styles.timeGrid}>
                {timeSlots.map((hour) => (
                    <div key={hour} className={styles.timeRow}>
                        <div className={styles.timeSlot}>
                            <Typography variant="caption" className={styles.timeLabel}>
                                {formatHour(hour)}
                            </Typography>
                        </div>
                        {week.days.map((day, dayIndex) => {
                            const dayEvents = getEventsForDateAndHour(day.date, hour);

                            return (
                                <div
                                        key={dayIndex}
                                        className={styles.hourCell}
                                        onClick={() => onTimeSlotClick?.(day.date, hour)}
                                >
                                    {dayEvents.map((event) => (
                                        <div
                                            key={event.id}
                                            className={styles.event}
                                            style={{backgroundColor: event.color}}
                                            onClick={(e) => e.stopPropagation()}
                                        >
                                            <Typography variant="small" className={styles.eventTitle}>
                                                {event.title}
                                            </Typography>
                                        </div>
                                    ))}
                                </div>
                            );
                        })}
                    </div>
                ))}
            </div>
        </div>
    );
};