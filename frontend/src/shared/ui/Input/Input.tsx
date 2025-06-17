import React, { forwardRef } from "react";
import styles from "./Input.module.scss";
import { cn } from "../../../lib/utils";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
    helperText?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
    ({ label, error, helperText, className, id, ...props }, ref) => {
        const inputId = id || `input-${Math.random().toString(36).substr(2, 9)}`;

        return (
            <div className={cn(styles.wrapper, className)}>
                {label && (
                    <label htmlFor={inputId} className={styles.label}>
                        {label}
                    </label>
                )}
                <input
                    ref={ref}
                    id={inputId}
                    className={cn(styles.input, {
                        [styles.error]: error,
                    })}
                    {...props}
                />
                {(error || helperText) && (
                    <span
                        className={cn(styles.helper, {
                            [styles.errorText]: error,
                        })}
                    >
            {error || helperText}
          </span>
                )}
            </div>
        );
    },
);

Input.displayName = "Input";