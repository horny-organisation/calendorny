export interface LoginFormData {
    email: string;
    password: string;
}

export interface RegisterFormData {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
}

export interface AuthUser {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
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