import React, { forwardRef } from "react";
import styles from "./Input.module.scss";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
    helperText?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
    ({ label, error, helperText, className, id, ...props }, ref) => {
        const inputId = id || `input-${Math.random().toString(36).substr(2, 9)}`;

        return (
            <div
                    className={[styles.wrapper, className || ""].filter(Boolean).join(" ")}
            >
                {label && (
                    <label htmlFor={inputId} className={styles.label}>
                        {label}
                    </label>
                )}
                <input
                    ref={ref}
                    id={inputId}
                    className={[styles.input, error ? styles.error : ""]
                        .filter(Boolean)
                        .join(" ")}
                    {...props}
                />
                {(error || helperText) && (
                    <span
                        className={[styles.helper, error ? styles.errorText : ""]
                            .filter(Boolean)
                            .join(" ")}
                    >
            {error || helperText}
          </span>
                )}
            </div>
        );
    },
);

Input.displayName = "Input";