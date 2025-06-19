import React from "react";
import { DAYS_OF_WEEK, generateCalendarMonth, isSameDay } from "../../../../entities/calendar";
import { Button } from "../../../../shared";
import { Typography } from "../../../../shared";
import styles from "./MiniCalendar.module.scss";

interface MiniCalendarProps {
    currentDate: Date;
    selectedDate?: Date | null;
    onDateSelect: (date: Date) => void;
    onNavigate: (direction: "prev" | "next") => void;
}

export const MiniCalendar: React.FC<MiniCalendarProps> = ({
                                                              currentDate,
                                                              selectedDate,
                                                              onDateSelect,
                                                              onNavigate,
                                                          }) => {
    const calendar = generateCalendarMonth(currentDate);

    return (
        <div className={styles.miniCalendar}>
            {/* Header */}
            <div className={styles.header}>
                <Button
                    variant="text"
                    size="small"
                    onClick={() => onNavigate("prev")}
                    className={styles.navButton}
                >
                    ‹
                </Button>

                <Typography variant="caption" className={styles.monthTitle}>
                    {calendar.monthName} {calendar.year}
                </Typography>

                <Button
                    variant="text"
                    size="small"
                    onClick={() => onNavigate("next")}
                    className={styles.navButton}
                >
                    ›
                </Button>
            </div>

            {/* Days of week header */}
            <div className={styles.weekHeader}>
                {DAYS_OF_WEEK.map((day) => (
                    <div key={day} className={styles.weekDay}>
                        <Typography variant="small">{day.slice(0, 1)}</Typography>
                    </div>
                ))}
            </div>

            {/* Calendar grid */}
            <div className={styles.calendarGrid}>
                {calendar.weeks.map((week, weekIndex) => (
                    <div key={weekIndex} className={styles.week}>
                        {week.days.map((day, dayIndex) => {
                            const isSelected =
                                selectedDate && isSameDay(day.date, selectedDate);

                            return (
                                <button
                                    key={dayIndex}
                                    className={`${styles.day} ${
                                        !day.isCurrentMonth ? styles.otherMonth : ""
                                    } ${day.isToday ? styles.today : ""} ${
                                        isSelected ? styles.selected : ""
                                    }`}
                                    onClick={() => onDateSelect(day.date)}
                                >
                                    <Typography variant="small" className={styles.dayNumber}>
                                        {day.date.getDate()}
                                    </Typography>
                                </button>
                            );
                        })}
                    </div>
                ))}
            </div>
        </div>
    );
};