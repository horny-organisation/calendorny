import React from "react";
import styles from "./Card.module.scss";
import { cn } from "../../../lib/utils";

interface CardProps {
    children: React.ReactNode;
    className?: string;
    padding?: "small" | "medium" | "large";
}

export const Card: React.FC<CardProps> = ({
                                              children,
                                              className,
                                              padding = "medium",
                                          }) => {
    return (
        <div className={cn(styles.card, styles[padding], className)}>
            {children}
        </div>
    );
};