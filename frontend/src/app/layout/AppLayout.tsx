import React from "react";
import { Outlet } from "react-router-dom";
import { AppHeader } from "./AppHeader/AppHeader";
import styles from "./AppLayout.module.scss";

export const AppLayout: React.FC = () => {
    return (
        <div className={styles.layout}>
            <AppHeader />
            <main className={styles.main}>
                <Outlet />
            </main>
        </div>
    );
};