import React from "react";
import styles from "./Button.module.scss";
import { cn } from "../../../lib/utils";

type ButtonVariant = "primary" | "secondary" | "text";
type ButtonSize = "small" | "medium" | "large";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: ButtonVariant;
    size?: ButtonSize;
    loading?: boolean;
    children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
                                                  variant = "primary",
                                                  size = "medium",
                                                  loading = false,
                                                  className,
                                                  children,
                                                  disabled,
                                                  ...props
                                              }) => {
    return (
        <button
            className={cn(
                styles.button,
                styles[variant],
                styles[size],
                {
                    [styles.loading]: loading,
                },
                className,
            )}
            disabled={disabled || loading}
            {...props}
        >
            {loading && <span className={styles.spinner} />}
            <span className={cn(styles.content, {[styles.hidden]: loading})}>
                {children}
            </span>
        </button>
    );
};