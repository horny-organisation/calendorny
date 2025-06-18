import React, { useState } from "react";
import { Input, Button, Card } from "../../../../shared";
import { Typography } from "../../../../shared";
import type { CalendarEvent } from "../../../../entities/calendar";
import styles from "./EventModal.module.scss";

interface EventModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSave: (event: Omit<CalendarEvent, "id">) => void;
    selectedDate?: Date;
    event?: CalendarEvent;
}

export const EventModal: React.FC<EventModalProps> = ({
                                                          isOpen,
                                                          onClose,
                                                          onSave,
                                                          selectedDate,
                                                          event,
                                                      }) => {
    const [formData, setFormData] = useState({
        title: event?.title || "",
        description: event?.description || "",
        startTime: event
            ? `${event.startDate.getHours().toString().padStart(2, "0")}:${event.startDate.getMinutes().toString().padStart(2, "0")}`
            : "09:00",
        endTime: event
            ? `${event.endDate.getHours().toString().padStart(2, "0")}:${event.endDate.getMinutes().toString().padStart(2, "0")}`
            : "10:00",
        color: event?.color || "#4285f4",
        isAllDay: event?.isAllDay || false,
    });

    const [errors, setErrors] = useState<Record<string, string>>({});

    if (!isOpen) return null;

    const handleInputChange = (field: string, value: string | boolean) => {
        setFormData((prev) => ({
            ...prev,
            [field]: value,
        }));

        // Очистить ошибку при изменении поля
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
            newErrors.title = "Введите название события";
        }

        if (!formData.isAllDay) {
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

        const baseDate = selectedDate || new Date();

        let startDate: Date;
        let endDate: Date;

        if (formData.isAllDay) {
            startDate = new Date(baseDate);
            startDate.setHours(0, 0, 0, 0);
            endDate = new Date(baseDate);
            endDate.setHours(23, 59, 59, 999);
        } else {
            const [startHour, startMinute] = formData.startTime
                .split(":")
                .map(Number);
            const [endHour, endMinute] = formData.endTime.split(":").map(Number);

            startDate = new Date(baseDate);
            startDate.setHours(startHour, startMinute, 0, 0);

            endDate = new Date(baseDate);
            endDate.setHours(endHour, endMinute, 0, 0);
        }

        const newEvent: Omit<CalendarEvent, "id"> = {
            title: formData.title.trim(),
            description: formData.description.trim(),
            startDate,
            endDate,
            color: formData.color,
            isAllDay: formData.isAllDay,
        };

        onSave(newEvent);
        onClose();
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
            <Card className={styles.modal} onClick={(e) => e.stopPropagation()}>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.header}>
                        <Typography variant="h3">
                            {event ? "Редактировать событие" : "Новое событие"}
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
                            label="Название"
                            placeholder="Введите название события"
                            value={formData.title}
                            onChange={(e) => handleInputChange("title", e.target.value)}
                            error={errors.title}
                        />

                        <Input
                            label="Описание"
                            placeholder="Описание события (необязательно)"
                            value={formData.description}
                            onChange={(e) => handleInputChange("description", e.target.value)}
                            error={errors.description}
                        />

                        <div className={styles.checkboxField}>
                            <label className={styles.checkboxLabel}>
                                <input
                                    type="checkbox"
                                    checked={formData.isAllDay}
                                    onChange={(e) =>
                                        handleInputChange("isAllDay", e.target.checked)
                                    }
                                    className={styles.checkbox}
                                />
                                <Typography variant="body">Весь день</Typography>
                            </label>
                        </div>

                        {!formData.isAllDay && (
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
                                        style={{ backgroundColor: option.value }}
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
                            {event ? "Сохранить" : "Создать"}
                        </Button>
                    </div>
                </form>
            </Card>
        </div>
    );
};