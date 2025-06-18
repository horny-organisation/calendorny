import React from "react";
import { Card } from "../shared";
import { RegisterForm } from "../feature/auth";
import styles from "./AuthPages.module.scss";

export const RegisterPage: React.FC = () => {
    return (
        <div className={styles.container}>
            <Card padding="large" className={styles.card}>
                <RegisterForm />
            </Card>
        </div>
    );
};