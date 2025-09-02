import { useState, useEffect } from "react";
import AuthForm from "../components/AuthForm.tsx";

const AuthPage = () => {
    const [isRegister, setIsRegister] = useState(false);

    const toggleMode = () => setIsRegister(!isRegister);

    // Підтримка postMessage для popup confirm
    useEffect(() => {
        const handler = (event: MessageEvent) => {
            const { type, payload } = event.data || {};
            if (type === "CONFIRM_ACCOUNT" && payload?.accessToken && payload?.refreshToken) {
                window.location.href = "/dashboard";
            }
        };
        window.addEventListener("message", handler);
        return () => window.removeEventListener("message", handler);
    }, []);

    return (
        <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
            <AuthForm isRegister={isRegister} toggleMode={toggleMode} />
        </div>
    );
};

export default AuthPage;
