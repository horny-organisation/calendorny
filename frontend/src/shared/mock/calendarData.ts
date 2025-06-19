import type { CalendarEvent } from "../../entities/calendar";

const today = new Date();
const tomorrow = new Date(today);
tomorrow.setDate(tomorrow.getDate() + 1);

const nextWeek = new Date(today);
nextWeek.setDate(nextWeek.getDate() + 7);

const nextMonth = new Date(today);
nextMonth.setMonth(nextMonth.getMonth() + 1);

export const mockEvents: CalendarEvent[] = [
    {
        id: "1",
        title: "Встреча с командой",
        description: "Обсуждение планов на следующую неделю",
        startDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            10,
            0,
        ),
        endDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            11,
            30,
        ),
        color: "#3b82f6",
        isAllDay: false,
        type: "event",
        location: "Офис, переговорная 2",
        attendees: ["ivan@example.com", "maria@example.com"],
        recurrence: "none",
    },
    {
        id: "2",
        title: "Завершить отчёт",
        description: "Подготовить ежемесячный отчёт по проекту",
        startDate: new Date(
            tomorrow.getFullYear(),
            tomorrow.getMonth(),
            tomorrow.getDate(),
            0,
            0,
        ),
        endDate: new Date(
            tomorrow.getFullYear(),
            tomorrow.getMonth(),
            tomorrow.getDate(),
            23,
            59,
        ),
        color: "#ef4444",
        isAllDay: true,
        type: "task",
        recurrence: "none",
    },
    {
        id: "3",
        title: "Обед с клиентом",
        description: "Обсуждение новых требований к проекту",
        startDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            13,
            0,
        ),
        endDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            14,
            30,
        ),
        color: "#10b981",
        isAllDay: false,
        type: "event",
        location: "Ресторан XYZ",
        recurrence: "none",
    },
    {
        id: "4",
        title: "Презентация для инвесторов",
        description: "Презентация результатов квартала",
        startDate: new Date(
            nextWeek.getFullYear(),
            nextWeek.getMonth(),
            nextWeek.getDate(),
            15,
            0,
        ),
        endDate: new Date(
            nextWeek.getFullYear(),
            nextWeek.getMonth(),
            nextWeek.getDate(),
            16,
            30,
        ),
        color: "#8b5cf6",
        isAllDay: false,
        type: "event",
        location: "Конференц-зал",
        attendees: ["ceo@example.com", "cfo@example.com"],
        recurrence: "none",
    },
    {
        id: "5",
        title: "Проверить email",
        description: "Ответить на важные письма",
        startDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            9,
            0,
        ),
        endDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            9,
            30,
        ),
        color: "#f59e0b",
        isAllDay: false,
        type: "task",
        recurrence: "daily",
    },
    {
        id: "6",
        title: "Еженедельная планёрка",
        description: "Синхронизация с командой",
        startDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            11,
            0,
        ),
        endDate: new Date(
            today.getFullYear(),
            today.getMonth(),
            today.getDate(),
            12,
            0,
        ),
        color: "#06b6d4",
        isAllDay: false,
        type: "event",
        location: "Zoom",
        recurrence: "weekly",
    },
];

// Функция для получения событий за период
export const getEventsForDateRange = (
    startDate: Date,
    endDate: Date,
): CalendarEvent[] => {
    return mockEvents.filter((event) => {
        const eventStart = new Date(event.startDate);
        const eventEnd = new Date(event.endDate);

        return (
            (eventStart >= startDate && eventStart <= endDate) ||
            (eventEnd >= startDate && eventEnd <= endDate) ||
            (eventStart <= startDate && eventEnd >= endDate)
        );
    });
};

// Функция для получения событий за день
export const getEventsForDate = (date: Date): CalendarEvent[] => {
    const startOfDay = new Date(date);
    startOfDay.setHours(0, 0, 0, 0);

    const endOfDay = new Date(date);
    endOfDay.setHours(23, 59, 59, 999);

    return getEventsForDateRange(startOfDay, endOfDay);
};

// Ф��нкция для сохранения нового события
export const saveEvent = (event: Omit<CalendarEvent, "id">): CalendarEvent => {
    const newEvent: CalendarEvent = {
        ...event,
        id: Date.now().toString(),
    };

    mockEvents.push(newEvent);
    return newEvent;
};

// Функция для удаления события
export const deleteEvent = (eventId: string): boolean => {
    const index = mockEvents.findIndex((event) => event.id === eventId);
    if (index > -1) {
        mockEvents.splice(index, 1);
        return true;
    }
    return false;
};