import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Input, Button } from "../../../../shared";
import { Typography } from "../../../../shared";
import { registerUser } from "../../../../shared";
import styles from "./RegisterForm.module.scss";

interface RegisterFormData {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
}

export const RegisterForm: React.FC = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState<RegisterFormData>({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
    });
    const [errors, setErrors] = useState<Partial<RegisterFormData>>({});
    const [isLoading, setIsLoading] = useState(false);
    const [serverError, setServerError] = useState<string | null>(null);
    const [registrationSuccess, setRegistrationSuccess] = useState(false);

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
                    setServerError(null);
                }
            };

    const validateForm = (): boolean => {
        const newErrors: Partial<RegisterFormData> = {};

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

        if (!formData.password) {
            newErrors.password = "Введите пароль";
        } else if (formData.password.length < 6) {
            newErrors.password = "Пароль должен содержать минимум 6 символов";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validateForm()) return;

        setIsLoading(true);
        setServerError(null);

        try {
            const result = await registerUser(formData);

            if (result.success) {
                setRegistrationSuccess(true);
                setTimeout(() => {
                    navigate("/login");
                }, 2000);
            } else {
                setServerError(result.message || "Ошибка регистрации");
            }
        } catch (error) {
            setServerError("Произошла ошибка. Попробуйте снова.");
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

                {registrationSuccess && (
                    <div className={styles.successMessage}>
                    Регистрация успешна! Перенаправляем на страницу входа...
                    </div>
                    )}

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
                    type="email"
                    label="Почта"
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