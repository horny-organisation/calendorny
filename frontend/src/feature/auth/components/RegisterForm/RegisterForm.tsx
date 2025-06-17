import React, { useState } from "react";
import { Link } from "react-router-dom";
import { Input, Button } from "@/shared/ui";
import { Typography } from "@/shared/Typography/Typography";
import { RegisterFormData } from "../../types";
import { authService } from "../../services/authService";
import styles from "./RegisterForm.module.scss";

export const RegisterForm: React.FC = () => {
    const [formData, setFormData] = useState<RegisterFormData>({
        fullName: "",
        email: "",
        password: "",
        confirmPassword: "",
    });
    const [errors, setErrors] = useState<Partial<RegisterFormData>>({});
    const [isLoading, setIsLoading] = useState(false);
    const [serverError, setServerError] = useState<string>("");

    const handleInputChange =
        (field: keyof RegisterFormData) =>
            (e: React.ChangeEvent<HTMLInputElement>) => {
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

                if (serverError) {
                    setServerError("");
                }
            };

    const validateForm = (): boolean => {
        const newErrors: Partial<RegisterFormData> = {};

        if (!formData.fullName.trim()) {
            newErrors.fullName = "Введите полное имя";
        } else if (formData.fullName.trim().length < 2) {
            newErrors.fullName = "Имя должно содержать минимум 2 символа";
        }

        if (!formData.email) {
            newErrors.email = "Введите email";
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = "Введите корректный email";
        }

        if (!formData.password) {
            newErrors.password = "Введите пароль";
        } else if (formData.password.length < 6) {
            newErrors.password = "Пароль должен содержать минимум 6 символов";
        }

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = "Подтвердите пароль";
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = "Пароли не совпадают";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validateForm()) return;

        setIsLoading(true);
        setServerError("");

        try {
            const user = await authService.register(formData);
            console.log("Успешная р��гистрация:", user);
            // Здесь должен быть редирект или сохранение состояния пользователя
        } catch (error) {
            setServerError(
                error instanceof Error ? error.message : "Произошла ошибка",
            );
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.header}>
                <Typography variant="h1" className={styles.title}>
                    Calendorny
                </Typography>
                <Typography variant="body" className={styles.description}>
                    Ваш персональный календарь задач для эффективного планирования
                </Typography>
            </div>

            <div className={styles.fields}>
                <Typography variant="h2" className={styles.formTitle}>
                    Создать аккаунт
                </Typography>

                {serverError && <div className={styles.serverError}>{serverError}</div>}

                <Input
                    type="text"
                    label="Полное имя"
                    placeholder="Введите ваше полное имя"
                    value={formData.fullName}
                    onChange={handleInputChange("fullName")}
                    error={errors.fullName}
                    autoComplete="name"
                />

                <Input
                    type="email"
                    label="Email"
                    placeholder="Введите ваш email"
                    value={formData.email}
                    onChange={handleInputChange("email")}
                    error={errors.email}
                    autoComplete="email"
                />

                <Input
                    type="password"
                    label="Пароль"
                    placeholder="Введите пароль"
                    value={formData.password}
                    onChange={handleInputChange("password")}
                    error={errors.password}
                    autoComplete="new-password"
                />

                <Input
                    type="password"
                    label="Подтверждение пароля"
                    placeholder="Повторите пароль"
                    value={formData.confirmPassword}
                    onChange={handleInputChange("confirmPassword")}
                    error={errors.confirmPassword}
                    autoComplete="new-password"
                />
            </div>

            <div className={styles.actions}>
                <Button
                    type="submit"
                    variant="primary"
                    size="large"
                    loading={isLoading}
                    className={styles.submitButton}
                >
                    Зарегистрироваться
                </Button>

                <div className={styles.linkSection}>
                    <Typography variant="caption">
                        Уже есть аккаунт?{" "}
                        <Link to="/login" className={styles.link}>
                            Войти
                        </Link>
                    </Typography>
                </div>
            </div>
        </form>
    );
};