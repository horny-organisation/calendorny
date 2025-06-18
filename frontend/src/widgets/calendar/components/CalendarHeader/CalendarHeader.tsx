import React from "react";
import { Button } from "../../../../shared";
import { Typography } from "../../../../shared";
import type { CalendarView } from "../../../../entities/calendar";
import styles from "./CalendarHeader.module.scss";

interface CalendarHeaderProps {
    currentDate: Date;
    view: CalendarView;
    onViewChange: (view: CalendarView) => void;
    onNavigate: (direction: "prev" | "next" | "today") => void;
    title: string;
}

export const CalendarHeader: React.FC<CalendarHeaderProps> = ({
                                                                  currentDate,
                                                                  view,
                                                                  onViewChange,
                                                                  onNavigate,
                                                                  title,
                                                              }) => {
    return (
        <div className={styles.header}>
            <div className={styles.navigation}>
                <Button
                    variant="text"
                    size="medium"
                    onClick={() => onNavigate("today")}
                    className={styles.todayButton}
                >
                    Сегодня
                </Button>

                <div className={styles.navButtons}>
                    <Button
                        variant="text"
                        size="small"
                        onClick={() => onNavigate("prev")}
                        className={styles.navButton}
                    >
                        &#8249;
                    </Button>
                    <Button
                        variant="text"
                        size="small"
                        onClick={() => onNavigate("next")}
                        className={styles.navButton}
                    >
                        &#8250;
                    </Button>
                </div>

                <Typography variant="h2" className={styles.title}>
                    {title}
                </Typography>
            </div>

            <div className={styles.viewSelector}>
                <Button
                    variant={view === "month" ? "primary" : "text"}
                    size="small"
                    onClick={() => onViewChange("month")}
                    className={styles.viewButton}
                >
                    Месяц
                </Button>
                <Button
                    variant={view === "week" ? "primary" : "text"}
                    size="small"
                    onClick={() => onViewChange("week")}
                    className={styles.viewButton}
                >
                    Неделя
                </Button>
            </div>
        </div>
    );
};