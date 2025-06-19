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

    const handleBrandClick = () => {
        navigate("/calendar");
    };

    if (isAuthPage) {
        return null;
    }

    return (
        <header className={styles.header}>
            <button className={styles.brandButton} onClick={handleBrandClick}>
                <Typography variant="h3" className={styles.brandText}>
                    Calendorny
                </Typography>
            </button>

            <div className={styles.actions}>
                {user && (
                    <button className={styles.userButton} onClick={handleProfileClick}>
                        <Typography variant="caption" className={styles.userInfo}>
                            {user.firstName} {user.lastName}
                        </Typography>
                    </button>
                )}
                <Button variant="text" size="medium" onClick={handleLogout}>
                    Выйти
                </Button>
            </div>
        </header>
    );
};