import React from "react";
import { Calendar } from "../widgets/calendar";
import type { CalendarEvent } from "../entities/calendar";
import styles from "./CalendarPage.module.scss";

// Mock данные для демонстрации
const mockEvents: CalendarEvent[] = [
    {
        id: "1",
        title: "Встреча с командой",
        description: "Обсуждение проекта Calendorny",
        startDate: new Date(2024, 11, 20, 10, 0),
        endDate: new Date(2024, 11, 20, 11, 30),
        color: "#4285f4",
        isAllDay: false,
        location: "Офис",
    },
    {
        id: "2",
        title: "Разработка фронтенда",
        description: "Работа над календарным виджетом",
        startDate: new Date(2024, 11, 20, 14, 0),
        endDate: new Date(2024, 11, 20, 17, 0),
        color: "#34a853",
        isAllDay: false,
    },
    {
        id: "3",
        title: "Планирование спринта",
        description: "Обсуждение задач на следующую неделю",
        startDate: new Date(2024, 11, 21, 9, 0),
        endDate: new Date(2024, 11, 21, 10, 0),
        color: "#ea4335",
        isAllDay: false,
    },
    {
        id: "4",
        title: "День рождения",
        startDate: new Date(2024, 11, 22, 0, 0),
        endDate: new Date(2024, 11, 22, 23, 59),
        color: "#ff9800",
        isAllDay: true,
    },
    {
        id: "5",
        title: "Код-ревью",
        description: "Проверка календарных компонентов",
        startDate: new Date(2024, 11, 23, 15, 0),
        endDate: new Date(2024, 11, 23, 16, 0),
        color: "#9c27b0",
        isAllDay: false,
    },
];

export const CalendarPage: React.FC = () => {
    const handleDateClick = (date: Date) => {
        console.log("Выбрана дата:", date);
    };

    const handleEventClick = (event: CalendarEvent) => {
        console.log("Выбрано событие:", event);
    };

    return (
        <div className={styles.calendarPage}>
            <Calendar
                events={mockEvents}
                onDateClick={handleDateClick}
                onEventClick={handleEventClick}
            />
        </div>
    );
};