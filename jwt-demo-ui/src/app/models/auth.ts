export interface LoginRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    accessToken: string;
    role: string;
}

export interface RegisterRequest {
    username: string;
    password: string;
    role?: string;
}

export interface RegisterResponse {
    message: string
}
