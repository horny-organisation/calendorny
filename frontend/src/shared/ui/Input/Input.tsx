import React, { forwardRef, useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import styles from "./Input.module.scss";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
    helperText?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
    ({ label, error, helperText, className, id, type, ...props }, ref) => {
        const inputId = id || `input-${Math.random().toString(36).substr(2, 9)}`;

        const [showPassword, setShowPassword] = useState(false);

        const isPasswordField = type === "password";
        const inputType = isPasswordField && showPassword ? "text" : type;

        const togglePasswordVisibility = () => {
            setShowPassword(!showPassword);
        };

        return (
            <div
                    className={[styles.wrapper, className || ""].filter(Boolean).join(" ")}
            >
                {label && (
                    <label htmlFor={inputId} className={styles.label}>
                        {label}
                    </label>
                )}
                <div className={styles.inputContainer}>
                    <input
                        ref={ref}
                        id={inputId}
                        type={inputType}
                        className={cn(styles.input, {
                            [styles.error]: error,
                            [styles.withIcon]: isPasswordField,
                        })}
                        {...props}
                    />
                    {isPasswordField && (
                        <button
                            type="button"
                            className={styles.toggleButton}
                            onClick={togglePasswordVisibility}
                            aria-label={showPassword ? "Скрыть пароль" : "Показать пароль"}
                        >
                            {showPassword ? (
                                <EyeOff className={styles.toggleIcon}/>
                            ) : (
                                <Eye className={styles.toggleIcon}/>
                            )}
                        </button>
                    )}
                </div>
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