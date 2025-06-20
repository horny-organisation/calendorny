import { http } from './authApi';

export interface UserProfile {
    firstName: string;
    lastName: string;
    birthDate: string;
    phoneNumber: string;
    email: string;
}

export interface UserProfileEdit {
    firstName?: string;
    lastName?: string;
    birthdate: string;
    phoneNumber?: string;
    timezone?: string;
    language?: string;
    telegram?: string;
}

export async function getProfile(): Promise<UserProfile> {
    const resp = await http.get<UserProfile>('/profile');
    return resp.data;
}

export async function updateProfile(payload: Partial<UserProfileEdit>): Promise<void> {
    await http.patch('/profile', payload);
}

export async function getProfileEditData(): Promise<UserProfileEdit> {
    const resp = await http.get<UserProfileEdit>('/profile/edit');
    return resp.data;
} 