import React, { useState } from "react";
import { MiniCalendar } from "../MiniCalendar/MiniCalendar";
import { Typography } from "../../../../shared";
import { addMonths } from "../../../../entities/calendar";
import styles from "./CalendarSidebar.module.scss";

interface CalendarSidebarProps {
    selectedDate?: Date | null;
    onDateSelect: (date: Date) => void;
}

export const CalendarSidebar: React.FC<CalendarSidebarProps> = ({
                                                                    selectedDate,
                                                                    onDateSelect,
                                                                }) => {
    const [miniCalendarDate, setMiniCalendarDate] = useState(new Date());

    const handleMiniCalendarNavigate = (direction: "prev" | "next") => {
        setMiniCalendarDate((prev) =>
            addMonths(prev, direction === "next" ? 1 : -1),
        );
    };

    return (
        <div className={styles.sidebar}>
            <div className={styles.sidebarContent}>
                <div className={styles.sidebarHeader}>
                    <Typography variant="caption" className={styles.sidebarTitle}>
                        Календарь
                    </Typography>
                </div>

                <MiniCalendar
                    currentDate={miniCalendarDate}
                    selectedDate={selectedDate}
                    onDateSelect={onDateSelect}
                    onNavigate={handleMiniCalendarNavigate}
                />
            </div>
        </div>
    );
};