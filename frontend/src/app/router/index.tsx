import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AppLayout } from "../layout/AppLayout";
import { LoginPage } from "../../pages/LoginPage";
import { RegisterPage } from "../../pages/RegisterPage";
import { CalendarPage } from "../../pages/CalendarPage";

export const AppRouter: React.FC = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<AppLayout />}>
                    <Route index element={<Navigate to="/calendar" replace />} />
                    <Route path="login" element={<LoginPage />} />
                    <Route path="register" element={<RegisterPage />} />
                    <Route path="calendar" element={<CalendarPage />} />
                    <Route path="*" element={<Navigate to="/calendar" replace />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
};