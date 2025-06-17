export interface LoginFormData {
    email: string;
    password: string;
}

export interface RegisterFormData {
    fullName: string;
    email: string;
    password: string;
    confirmPassword: string;
}

export interface AuthUser {
    id: string;
    email: string;
    fullName: string;
}

export interface AuthState {
    user: AuthUser | null;
    isLoading: boolean;
    error: string | null;
}

export interface AuthService {
    login: (data: LoginFormData) => Promise<AuthUser>;
    register: (data: RegisterFormData) => Promise<AuthUser>;
    logout: () => Promise<void>;
}