import React, { useState } from "react";
import { Input, Button } from "../../../../shared";
import { Typography } from "../../../../shared";
import type { CalendarEvent, EventType } from "../../../../entities/calendar";
import styles from "./EventCreationModal.module.scss";

interface EventCreationModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSave: (event: Omit<CalendarEvent, "id">) => void;
    selectedDate: Date;
    selectedHour?: number;
    type: EventType;
}

export const EventCreationModal: React.FC<EventCreationModalProps> = ({
                                                                          isOpen,
                                                                          onClose,
                                                                          onSave,
                                                                          selectedDate,
                                                                          selectedHour,
                                                                          type,
                                                                      }) => {
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        startTime: selectedHour
            ? `${selectedHour.toString().padStart(2, "0")}:00`
            : "09:00",
        endTime: selectedHour
            ? `${(selectedHour + 1).toString().padStart(2, "0")}:00`
            : "10:00",
        recurrence: "none" as const,
        color: "#4285f4",
    });

    const [errors, setErrors] = useState<Record<string, string>>({});

    if (!isOpen) return null;

    const isTask = type === "task";

    const handleInputChange = (field: string, value: string) => {
        setFormData((prev) => ({
            ...prev,
            [field]: value,
        }));

        if (errors[field]) {
            setErrors((prev) => ({
                ...prev,
                [field]: "",
            }));
        }
    };

    const validateForm = (): boolean => {
        const newErrors: Record<string, string> = {};

        if (!formData.title.trim()) {
            newErrors.title = `Введите название ${isTask ? "задачи" : "мероприятия"}`;
        }

        if (!isTask) {
            if (!formData.startTime) {
                newErrors.startTime = "Введите время начала";
            }
            if (!formData.endTime) {
                newErrors.endTime = "Введите время окончания";
            }
            if (
                formData.startTime &&
                formData.endTime &&
                formData.startTime >= formData.endTime
            ) {
                newErrors.endTime = "Время окончания должно быть позже времени начала";
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();

        if (!validateForm()) return;

        let startDate: Date;
        let endDate: Date;

        if (isTask) {
            // Задача на весь день
            startDate = new Date(selectedDate);
            startDate.setHours(0, 0, 0, 0);
            endDate = new Date(selectedDate);
            endDate.setHours(23, 59, 59, 999);
        } else {
            // Мероприятие с конкретным временем
            const [startHour, startMinute] = formData.startTime
                .split(":")
                .map(Number);
            const [endHour, endMinute] = formData.endTime.split(":").map(Number);

            startDate = new Date(selectedDate);
            startDate.setHours(startHour, startMinute, 0, 0);

            endDate = new Date(selectedDate);
            endDate.setHours(endHour, endMinute, 0, 0);
        }

        const newEvent: Omit<CalendarEvent, "id"> = {
            title: formData.title.trim(),
            description: formData.description.trim(),
            startDate,
            endDate,
            color: formData.color,
            isAllDay: isTask,
            type,
            recurrence: formData.recurrence,
        };

        onSave(newEvent);
        onClose();

        // Сброс формы
        setFormData({
            title: "",
            description: "",
            startTime: "09:00",
            endTime: "10:00",
            recurrence: "none",
            color: "#4285f4",
        });
        setErrors({});
    };

    const colorOptions = [
        { value: "#4285f4", label: "Синий" },
        { value: "#34a853", label: "Зеленый" },
        { value: "#ea4335", label: "Красный" },
        { value: "#ff9800", label: "Оранжевый" },
        { value: "#9c27b0", label: "Фиолетовый" },
        { value: "#795548", label: "Коричневый" },
    ];

    return (
        <div className={styles.overlay} onClick={onClose}>
            <div
                    className={styles.modal}
                    onClick={(e: React.MouseEvent) => e.stopPropagation()}
            >
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.header}>
                        <Typography variant="h3">
                            {isTask ? "Новая задача" : "Новое мероприятие"}
                        </Typography>
                        <Button
                            type="button"
                            variant="text"
                            size="small"
                            onClick={onClose}
                            className={styles.closeButton}
                        >
                            ✕
                        </Button>
                    </div>

                    <div className={styles.fields}>
                        <Input
                            label={`Название ${isTask ? "задачи" : "мероприятия"}`}
                            placeholder={`Введите название ${isTask ? "задачи" : "мероприятия"}`}
                            value={formData.title}
                            onChange={(e) => handleInputChange("title", e.target.value)}
                            error={errors.title}
                            autoFocus
                        />

                        <Input
                            label="Описание"
                            placeholder="Описание (необязательно)"
                            value={formData.description}
                            onChange={(e) => handleInputChange("description", e.target.value)}
                            error={errors.description}
                        />

                        {!isTask && (
                            <div className={styles.timeFields}>
                                <Input
                                    type="time"
                                    label="Время начала"
                                    value={formData.startTime}
                                    onChange={(e) =>
                                        handleInputChange("startTime", e.target.value)
                                    }
                                    error={errors.startTime}
                                />
                                <Input
                                    type="time"
                                    label="Время окончания"
                                    value={formData.endTime}
                                    onChange={(e) => handleInputChange("endTime", e.target.value)}
                                    error={errors.endTime}
                                />
                            </div>
                        )}

                        {!isTask && (
                            <div className={styles.recurrenceField}>
                                <label htmlFor="recurrence" className={styles.label}>
                                    Регулярность
                                </label>
                                <select
                                    id="recurrence"
                                    value={formData.recurrence}
                                    onChange={(e) =>
                                        handleInputChange("recurrence", e.target.value)
                                    }
                                    className={styles.select}
                                >
                                    <option value="none">Не повторять</option>
                                    <option value="daily">Ежедневно</option>
                                    <option value="weekly">Еженедельно</option>
                                    <option value="monthly">Ежемесячно</option>
                                </select>
                            </div>
                        )}

                        <div className={styles.colorField}>
                            <Typography variant="caption" className={styles.colorLabel}>
                                Цвет
                            </Typography>
                            <div className={styles.colorOptions}>
                                {colorOptions.map((option) => (
                                    <button
                                        key={option.value}
                                        type="button"
                                        className={`${styles.colorOption} ${
                                            formData.color === option.value ? styles.selected : ""
                                        }`}
                                        style={{backgroundColor: option.value}}
                                        onClick={() => handleInputChange("color", option.value)}
                                        title={option.label}
                                    />
                                ))}
                            </div>
                        </div>
                    </div>

                    <div className={styles.actions}>
                        <Button
                            type="button"
                            variant="text"
                            size="medium"
                            onClick={onClose}
                        >
                            Отмена
                        </Button>
                        <Button type="submit" variant="primary" size="medium">
                            {isTask ? "Создать задачу" : "Создать мероприятие"}
                        </Button>
                    </div>
                </form>
            </div>
        </div>
    );
};