import React from "react";
import styles from "./Typography.module.scss";
import { cn } from "../../lib/utils";

type TypographyVariant = "h1" | "h2" | "h3" | "body" | "caption" | "small";

interface TypographyProps {
    variant: TypographyVariant;
    children: React.ReactNode;
    className?: string;
    as?: React.ElementType;
}

export const Typography: React.FC<TypographyProps> = ({
                                                          variant,
                                                          children,
                                                          className,
                                                          as,
                                                      }) => {
    const Component = as || getDefaultComponent(variant);

    return <Component className={cn(styles[variant], className)}>{children}</Component>;
};

function getDefaultComponent(variant: TypographyVariant): React.ElementType {
    switch (variant) {
        case "h1":
            return "h1";
        case "h2":
            return "h2";
        case "h3":
            return "h3";
        case "body":
            return "p";
        case "caption":
            return "span";
        case "small":
            return "small";
        default:
            return "p";
    }
}