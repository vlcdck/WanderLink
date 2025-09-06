import {useState} from "react";
import {useDispatch} from "react-redux";
import {useNavigate} from "react-router";
import api from "../api/api";
import {setTokens} from "../redux/slices/authSlice";
import axios from "axios";
import {loginSchema, registerSchema} from "../validation/authValidation";
import type {AuthFormData} from "../types/AuthFormData.ts";

export const useAuth = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const register = async (data: AuthFormData) => {
        setLoading(true);
        setError(null);

        // ✅ Валідація реєстрації
        const {error: validationError} = registerSchema.validate(data);
        if (validationError) {
            setLoading(false);
            setError(validationError.details[0].message);
            return;
        }

        try {
            await api.post("/auth/register", data);
            sessionStorage.setItem("registeredEmail", data.email);
            navigate("/check-email");
        } catch (err: unknown) {
            if (axios.isAxiosError(err)) {
                setError(err.response?.data?.message || "Помилка реєстрації");
            } else {
                setError("Помилка реєстрації");
            }
        } finally {
            setLoading(false);
        }
    };

    const login = async (data: AuthFormData) => {
        setLoading(true);
        setError(null);

        // ✅ Валідація логіну тільки потрібних полів
        const {error: validationError} = loginSchema.validate({
            email: data.email,
            password: data.password,
        });

        if (validationError) {
            setLoading(false);
            setError(validationError.details[0].message);
            return;
        }

        try {
            const res = await api.post("/auth/login", {
                email: data.email,
                password: data.password,
            });
            dispatch(
                setTokens({
                    accessToken: res.data.accessToken,
                    refreshToken: res.data.refreshToken,
                })
            );
            navigate("/dashboard");
        } catch (err: unknown) {
            if (axios.isAxiosError(err)) {
                const msg = err.response?.data?.message;
                setError(msg || "Невірний email або пароль");
            } else {
                setError("Невірний email або пароль");
            }
        } finally {
            setLoading(false);
        }
    };

    return {register, login, loading, error, setError};
};
