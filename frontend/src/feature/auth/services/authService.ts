import type {AuthUser, LoginFormData, RegisterFormData} from "../types";

class AuthService {
    async login(data: LoginFormData): Promise<AuthUser> {

        await new Promise((resolve) => setTimeout(resolve, 1000));


        if (data.email === "demo@example.com" && data.password === "demo123") {
            return {
                id: "1",
                email: data.email,
                fullName: "Демо Пользователь",
            };
        }

        throw new Error("Неверный email или пароль");
    }

    async register(data: RegisterFormData): Promise<AuthUser> {

        await new Promise((resolve) => setTimeout(resolve, 1000));


        if (data.password !== data.confirmPassword) {
            throw new Error("Пароли не совпадают");
        }

        if (data.password.length < 6) {
            throw new Error("Пароль должен содержать минимум 6 символов");
        }

        return {
            id: "2",
            email: data.email,
            fullName: data.fullName,
        };
    }

    async logout(): Promise<void> {
        await new Promise((resolve) => setTimeout(resolve, 500));
    }
}

export const authService = new AuthService();