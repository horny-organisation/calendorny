import type { CalendarDay, CalendarWeek, CalendarMonth } from "../types";

export const DAYS_OF_WEEK = ["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"];

export const MONTHS = [
    "Январь",
    "Февраль",
    "Март",
    "Апрель",
    "Май",
    "Июнь",
    "Июль",
    "Август",
    "Сентябрь",
    "Октябрь",
    "Ноябрь",
    "Декабрь",
];

export function getStartOfWeek(date: Date): Date {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1); // Adjust when day is Sunday
    return new Date(d.setDate(diff));
}

export function getEndOfWeek(date: Date): Date {
    const startOfWeek = getStartOfWeek(date);
    const endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(startOfWeek.getDate() + 6);
    return endOfWeek;
}

export function getStartOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth(), 1);
}

export function getEndOfMonth(date: Date): Date {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0);
}

export function isSameDay(date1: Date, date2: Date): boolean {
    return (
        date1.getDate() === date2.getDate() &&
        date1.getMonth() === date2.getMonth() &&
        date1.getFullYear() === date2.getFullYear()
    );
}

export function isToday(date: Date): boolean {
    return isSameDay(date, new Date());
}

export function addDays(date: Date, days: number): Date {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
}

export function addWeeks(date: Date, weeks: number): Date {
    return addDays(date, weeks * 7);
}

export function addMonths(date: Date, months: number): Date {
    const result = new Date(date);
    result.setMonth(result.getMonth() + months);
    return result;
}

export function generateCalendarMonth(date: Date): CalendarMonth {
    const startOfMonth = getStartOfMonth(date);
    const endOfMonth = getEndOfMonth(date);
    const startOfCalendar = getStartOfWeek(startOfMonth);
    const endOfCalendar = getEndOfWeek(endOfMonth);

    const weeks: CalendarWeek[] = [];
    let current = new Date(startOfCalendar);

    while (current <= endOfCalendar) {
        const days: CalendarDay[] = [];
        for (let i = 0; i < 7; i++) {
            days.push({
                date: new Date(current),
                isCurrentMonth:
                    current.getMonth() === date.getMonth() &&
                    current.getFullYear() === date.getFullYear(),
                isToday: isToday(current),
                isSelected: false,
                events: [],
            });
            current = addDays(current, 1);
        }
        weeks.push({ days });
    }

    return {
        weeks,
        monthName: MONTHS[date.getMonth()],
        year: date.getFullYear(),
    };
}

export function generateCalendarWeek(date: Date): CalendarWeek {
    const startOfWeek = getStartOfWeek(date);
    const days: CalendarDay[] = [];

    for (let i = 0; i < 7; i++) {
        const current = addDays(startOfWeek, i);
        days.push({
            date: current,
            isCurrentMonth: true,
            isToday: isToday(current),
            isSelected: isSameDay(current, date),
            events: [],
        });
    }

    return { days };
}

export function formatDateForDisplay(date: Date): string {
    return `${date.getDate()} ${MONTHS[date.getMonth()]} ${date.getFullYear()}`;
}

export function formatTimeForDisplay(date: Date): string {
    return date.toLocaleTimeString("ru-RU", {
        hour: "2-digit",
        minute: "2-digit",
    });
}