export type EventType = "event" | "task";

export interface CalendarEvent {
    id: string;
    title: string;
    description?: string;
    startDate: Date;
    endDate: Date;
    color: string;
    isAllDay: boolean;
    type: EventType;
    location?: string;
    attendees?: string[];
    recurrence?: "none" | "daily" | "weekly" | "monthly";
}

export interface CalendarDay {
    date: Date;
    isCurrentMonth: boolean;
    isToday: boolean;
    isSelected: boolean;
    events: CalendarEvent[];
}

export interface CalendarWeek {
    days: CalendarDay[];
}

export interface CalendarMonth {
    weeks: CalendarWeek[];
    monthName: string;
    year: number;
}

export type CalendarView = "month" | "week";

export interface CalendarState {
    currentDate: Date;
    view: CalendarView;
    events: CalendarEvent[];
    selectedDate: Date | null;
}