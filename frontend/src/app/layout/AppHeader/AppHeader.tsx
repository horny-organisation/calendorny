import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Button } from "../../../shared";
import { Typography } from "../../../shared";
import styles from "./AppHeader.module.scss";

export const AppHeader: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const isAuthPage = ["/login", "/register"].includes(location.pathname);

    const handleLogout = () => {
        // Здесь должна быть логика выхода из системы
        console.log("Выход из системы");
        navigate("/login");
    };

    if (isAuthPage) {
        return null;
    }

    return (
        <header className={styles.header}>
            <div className={styles.brand}>
                <Typography variant="h3" className={styles.brandText}>
                    Calendorny
                </Typography>
            </div>

            <div className={styles.actions}>
                <Button variant="text" size="medium" onClick={handleLogout}>
                    Выйти
                </Button>
            </div>
        </header>
    );
};