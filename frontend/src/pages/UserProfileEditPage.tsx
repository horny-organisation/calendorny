import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Card, Button, Input } from "../shared";
import { Typography } from "../shared";
import { getCurrentUser, updateUser } from "../shared";
import { ArrowLeft, Save, X } from "lucide-react";
import styles from "./UserProfileEditPage.module.scss";

interface EditFormData {
    firstName: string;
    lastName: string;
    dateOfBirth: string;
    phoneNumber: string;
}

export const UserProfileEditPage: React.FC = () => {
    const navigate = useNavigate();
    const user = getCurrentUser();

    const [formData, setFormData] = useState<EditFormData>(() => ({
        firstName: user?.firstName || "",
        lastName: user?.lastName || "",
        dateOfBirth: user?.dateOfBirth || "",
        phoneNumber: user?.phoneNumber || "",
    }));
    const [errors, setErrors] = useState<Partial<EditFormData>>({});
    const [isLoading, setIsLoading] = useState(false);
    const [saveError, setSaveError] = useState<string | null>(null);

    useEffect(() => {
        if (user) {
            setFormData({
                firstName: user.firstName,
                lastName: user.lastName,
                dateOfBirth: user.dateOfBirth || "",
                phoneNumber: user.phoneNumber || "",
            });
        }
    }, [user]);

    if (!user) {
        return (
            <div className={styles.container}>
                <Card padding="large" className={styles.errorCard}>
                    <Typography variant="h2">Ошибка</Typography>
                    <Typography variant="body">
                        Не удалось загрузить данные пользователя
                    </Typography>
                </Card>
            </div>
        );
    }

    const handleInputChange =
        (field: keyof EditFormData) => (e: React.ChangeEvent<HTMLInputElement>) => {
            setFormData((prev) => ({
                ...prev,
                [field]: e.target.value,
            }));

            // Очистить ошибку при изменении поля
            if (errors[field]) {
                setErrors((prev) => ({
                    ...prev,
                    [field]: undefined,
                }));
            }

            if (saveError) {
                setSaveError(null);
            }
        };

    const validateForm = (): boolean => {
        const newErrors: Partial<EditFormData> = {};

        if (!formData.firstName.trim()) {
            newErrors.firstName = "Введите имя";
        } else if (formData.firstName.trim().length < 2) {
            newErrors.firstName = "Имя должно содержать минимум 2 символа";
        }

        if (!formData.lastName.trim()) {
            newErrors.lastName = "Введите фамилию";
        } else if (formData.lastName.trim().length < 2) {
            newErrors.lastName = "Фамилия должна содержать минимум 2 символа";
        }

        // Валидация даты рождения (если указана)
        if (formData.dateOfBirth) {
            const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
            if (!dateRegex.test(formData.dateOfBirth)) {
                newErrors.dateOfBirth = "Введите дату в формате ГГГГ-ММ-ДД";
            } else {
                const date = new Date(formData.dateOfBirth);
                const today = new Date();
                if (date > today) {
                    newErrors.dateOfBirth = "Дата рождения не может быть в будущем";
                }
            }
        }

        // Валидация телефона (если указан)
        if (formData.phoneNumber) {
            const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
            if (!phoneRegex.test(formData.phoneNumber.replace(/[\s\-\(\)]/g, ""))) {
                newErrors.phoneNumber = "Введите корректный номер телефона";
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validateForm()) return;

        setIsLoading(true);
        setSaveError(null);

        try {
            const result = await updateUser(user.id, {
                firstName: formData.firstName.trim(),
                lastName: formData.lastName.trim(),
                dateOfBirth: formData.dateOfBirth || undefined,
                phoneNumber: formData.phoneNumber || undefined,
            });

            if (result.success) {
                navigate("/profile");
            } else {
                setSaveError(result.message || "Ошибка сохранения");
            }
        } catch (error) {
            setSaveError("Произошла ошибка. Попробуйте снова.");
        } finally {
            setIsLoading(false);
        }
    };

    const handleCancel = () => {
        navigate("/profile");
    };

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <button className={styles.backButton} onClick={handleCancel}>
                    <ArrowLeft className={styles.backIcon} />
                    <Typography variant="body" className={styles.backText}>
                        Назад к профилю
                    </Typography>
                </button>
                <Typography variant="h1" className={styles.title}>
                    Редактирование профиля
                </Typography>
            </div>

            <Card padding="large" className={styles.formCard}>
                <form onSubmit={handleSave} className={styles.form}>
                    {saveError && <div className={styles.errorMessage}>{saveError}</div>}

                    <div className={styles.fieldsGrid}>
                        <Input
                            type="text"
                            label="Имя"
                            placeholder="Введите ваше имя"
                            value={formData.firstName}
                            onChange={handleInputChange("firstName")}
                            error={errors.firstName}
                            autoComplete="given-name"
                        />

                        <Input
                            type="text"
                            label="Фамилия"
                            placeholder="Введите вашу фамилию"
                            value={formData.lastName}
                            onChange={handleInputChange("lastName")}
                            error={errors.lastName}
                            autoComplete="family-name"
                        />

                        <Input
                            type="date"
                            label="Дата рождения"
                            value={formData.dateOfBirth}
                            onChange={handleInputChange("dateOfBirth")}
                            error={errors.dateOfBirth}
                            autoComplete="bday"
                        />

                        <Input
                            type="tel"
                            label="Номер телефона"
                            placeholder="+7 (xxx) xxx-xx-xx"
                            value={formData.phoneNumber}
                            onChange={handleInputChange("phoneNumber")}
                            error={errors.phoneNumber}
                            autoComplete="tel"
                        />
                    </div>

                    <div className={styles.actions}>
                        <Button
                            type="button"
                            variant="text"
                            size="large"
                            onClick={handleCancel}
                            disabled={isLoading}
                            className={styles.cancelButton}
                        >
                            <X className={styles.buttonIcon} />
                            Отмена
                        </Button>

                        <Button
                            type="submit"
                            variant="primary"
                            size="large"
                            loading={isLoading}
                            className={styles.saveButton}
                        >
                            <Save className={styles.buttonIcon} />
                            Сохранить изменения
                        </Button>
                    </div>
                </form>
            </Card>
        </div>
    );
};