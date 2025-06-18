import React, { useState } from "react";
import {
    type CalendarView,
    type CalendarEvent,
    generateCalendarMonth,
    generateCalendarWeek,
    addMonths,
    addWeeks,
    formatDateForDisplay,
    MONTHS,
} from "../../../../entities/calendar";
import { CalendarHeader } from "../CalendarHeader/CalendarHeader";
import { MonthView } from "../MonthView/MonthView";
import { WeekView } from "../WeekView/WeekView";
import styles from "./Calendar.module.scss";

interface CalendarProps {
    events?: CalendarEvent[];
    onDateClick?: (date: Date) => void;
    onEventClick?: (event: CalendarEvent) => void;
}

export const Calendar: React.FC<CalendarProps> = ({
                                                      events = [],
                                                      onDateClick,
                                                      onEventClick,
                                                  }) => {
    const [currentDate, setCurrentDate] = useState(new Date());
    const [view, setView] = useState<CalendarView>("month");
    const [selectedDate, setSelectedDate] = useState<Date | null>(null);

    const handleDateClick = (date: Date) => {
        setSelectedDate(date);
        onDateClick?.(date);
    };

    const handleNavigate = (direction: "prev" | "next" | "today") => {
        if (direction === "today") {
            setCurrentDate(new Date());
            return;
        }

        if (view === "month") {
            setCurrentDate((prev) => addMonths(prev, direction === "next" ? 1 : -1));
        } else {
            setCurrentDate((prev) => addWeeks(prev, direction === "next" ? 1 : -1));
        }
    };

    const getTitle = (): string => {
        if (view === "month") {
            return `${MONTHS[currentDate.getMonth()]} ${currentDate.getFullYear()}`;
        } else {
            const week = generateCalendarWeek(currentDate);
            const firstDay = week.days[0].date;
            const lastDay = week.days[6].date;

            if (firstDay.getMonth() === lastDay.getMonth()) {
                return `${firstDay.getDate()}-${lastDay.getDate()} ${MONTHS[firstDay.getMonth()]} ${firstDay.getFullYear()}`;
            } else {
                return `${formatDateForDisplay(firstDay)} - ${formatDateForDisplay(lastDay)}`;
            }
        }
    };

    const calendar = generateCalendarMonth(currentDate);
    const week = generateCalendarWeek(currentDate);

    return (
        <div className={styles.calendar}>
            <CalendarHeader
                currentDate={currentDate}
                view={view}
                onViewChange={setView}
                onNavigate={handleNavigate}
                title={getTitle()}
            />

            <div className={styles.calendarContent}>
                {view === "month" ? (
                    <MonthView
                        calendar={calendar}
                        events={events}
                        onDateClick={handleDateClick}
                        selectedDate={selectedDate}
                    />
                ) : (
                    <WeekView
                        week={week}
                        events={events}
                        onDateClick={handleDateClick}
                        selectedDate={selectedDate}
                    />
                )}
            </div>
        </div>
    );
};