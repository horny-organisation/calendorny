import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Button } from "../../../shared";
import { Typography } from "../../../shared";
import { getCurrentUser, logoutUser } from "../../../shared";
import styles from "./AppHeader.module.scss";

export const AppHeader: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const user = getCurrentUser();

    const isAuthPage = ["/login", "/register"].includes(location.pathname);

    const handleLogout = () => {
        logoutUser();
        navigate("/login");
    };

    const handleProfileClick = () => {
        navigate("/profile");
    };

    if (isAuthPage) {
        return null;
    }

    return (
        <header className={styles.header}>
            <div className={styles.brand}>
                <button className={styles.userButton} onClick={handleProfileClick}>
                    <Typography variant="caption" className={styles.userInfo}>
                        {user.firstName} {user.lastName}
                    </Typography>
                </button>
            </div>

            <div className={styles.actions}>
                {user && (
                    <Typography variant="caption" className={styles.userInfo}>
                        {user.firstName} {user.lastName}
                    </Typography>
                )}
                <Button variant="text" size="medium" onClick={handleLogout}>
                    Выйти
                </Button>
            </div>
        </header>
    );
};