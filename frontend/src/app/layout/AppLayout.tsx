import React from "react";
import { Outlet } from "react-router-dom";
import styles from "./AppLayout.module.scss";

export const AppLayout: React.FC = () => {
    return (
        <div className={styles.layout}>
            <main className={styles.main}>
                <Outlet />
            </main>
        </div>
    );
};