import axios from 'axios';

export interface RegisterRequest {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
}

export interface RegisterResponse {
    userId: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface AuthTokens {
    accessToken: string;
    refreshToken?: string | null;
}

export interface RefreshRequest {

}

export interface ApiErrorResponse {
    code: number;
    exceptionName: string;
    exceptionMessage: string;
}

const AUTH_SERVER = 'http://localhost:8080/auth/api/v1';

const http = axios.create({
    baseURL: AUTH_SERVER,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

export async function registerApi(payload: RegisterRequest): Promise<RegisterResponse> {
    const resp = await http.post<RegisterResponse>('/register', payload);
    return resp.data;
}

export async function loginApi(payload: LoginRequest): Promise<AuthTokens> {
    const resp = await http.post<AuthTokens>('/login', payload);
    return resp.data;
}

export async function refreshApi(): Promise<AuthTokens> {
    const resp = await http.post<AuthTokens>('/refresh', {});
    return resp.data;
}

export async function logoutApi(): Promise<void> {
    await http.post('/logout', {});
}

registerApi({
    email: "test@example.com",
    password: "password123",
    firstName: "Иван",
    lastName: "Иванов"
})
.then(res => console.log("Успех:", res))
.catch(err => console.error("Ошибка:", err));

export { http }; 