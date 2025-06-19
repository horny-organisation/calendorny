import React from "react";
import { useNavigate } from "react-router-dom";
import { Card, Button } from "../shared";
import { Typography } from "../shared";
import { getCurrentUser } from "../shared";
import { User } from "lucide-react";
import styles from "./UserProfilePage.module.scss";

export const UserProfilePage: React.FC = () => {
    const navigate = useNavigate();
    const user = getCurrentUser();

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

    const handleEditProfile = () => {
        navigate("/profile/edit");
    };

    return (
        <div className={styles.container}>
        <div className={styles.header}>
        <div className={styles.headerContent}>
        <div className={styles.avatar}>
        <User className={styles.avatarIcon} />
    </div>
    <div className={styles.headerInfo}>
    <Typography variant="h1" className={styles.userName}>
        {user.firstName} {user.lastName}
    </Typography>
    <Typography variant="body" className={styles.userEmail}>
        {user.email}
        </Typography>
        </div>
        </div>
        <Button
    variant="primary"
    size="medium"
    onClick={handleEditProfile}
    className={styles.editButton}
        >
        Редактировать данные
    </Button>
    </div>

    <Card padding="large" className={styles.profileCard}>
    <Typography variant="h2" className={styles.sectionTitle}>
        Личная информация
    </Typography>

    <div className={styles.infoGrid}>
    <div className={styles.infoItem}>
    <Typography variant="caption" className={styles.infoLabel}>
        Имя
        </Typography>
        <Typography variant="body" className={styles.infoValue}>
        {user.firstName}
        </Typography>
        </div>

        <div className={styles.infoItem}>
    <Typography variant="caption" className={styles.infoLabel}>
        Фамилия
        </Typography>
        <Typography variant="body" className={styles.infoValue}>
        {user.lastName}
        </Typography>
        </div>

        <div className={styles.infoItem}>
    <Typography variant="caption" className={styles.infoLabel}>
        Электронная почта
    </Typography>
    <Typography variant="body" className={styles.infoValue}>
        {user.email}
        </Typography>
        </div>

        <div className={styles.infoItem}>
    <Typography variant="caption" className={styles.infoLabel}>
        Дата рождения
    </Typography>
    <Typography variant="body" className={styles.infoValue}>
        {user.dateOfBirth || (
                <span className={styles.emptyValue}>Не указана</span>
)}
    </Typography>
    </div>

    <div className={styles.infoItem}>
    <Typography variant="caption" className={styles.infoLabel}>
        Номер телефона
    </Typography>
    <Typography variant="body" className={styles.infoValue}>
        {user.phoneNumber || (
                <span className={styles.emptyValue}>Не указан</span>
)}
    </Typography>
    </div>
    </div>
    </Card>
    </div>
);
};