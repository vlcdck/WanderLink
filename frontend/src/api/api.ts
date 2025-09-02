import axios, {type AxiosError, type AxiosResponse, type InternalAxiosRequestConfig} from "axios";
import {authStorage} from "../lib/authStorage";
import {store} from "../redux/store";
import {clearTokens, setTokens} from "../redux/slices/authSlice";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json",
    },
});

// Додаємо Authorization через метод .set
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const token = authStorage.getAccessToken();
    if (token && config.headers) {
        config.headers.set("Authorization", `Bearer ${token}`);
    }
    return config;
});

// Обробка 401 та refresh токена
api.interceptors.response.use(
    (response: AxiosResponse) => response,
    async (error: AxiosError & { config?: InternalAxiosRequestConfig & { _retry?: boolean } }) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = authStorage.getRefreshToken();
                if (!refreshToken) return Promise.reject(new Error("No refresh token"));


                const res = await axios.post("http://localhost:8080/api/auth/refresh", {refreshToken});
                const {accessToken, refreshToken: newRefresh} = res.data;

                // Зберігаємо токени
                store.dispatch(setTokens({accessToken, refreshToken: newRefresh}));

                // Оновлюємо Authorization
                if (originalRequest.headers) {
                    originalRequest.headers.set("Authorization", `Bearer ${accessToken}`);
                }

                return api(originalRequest);
            } catch {
                store.dispatch(clearTokens());
                return Promise.reject(error);
            }
        }

        return Promise.reject(error);
    }
);

export default api;
