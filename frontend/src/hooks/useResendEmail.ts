import { useState } from "react";
import api from "../api/api";

export const useResendEmail = () => {
    const [resending, setResending] = useState(false);
    const [message, setMessage] = useState<string | null>(null);

    const resend = async () => {
        const email = sessionStorage.getItem("registeredEmail");
        if (!email) return;

        setResending(true);
        setMessage(null);

        try {
            await api.post("/auth/resend-confirmation", null, { params: { email } });
            setMessage("Лист повторно відправлено ✅");
            sessionStorage.removeItem("registeredEmail");
        } catch {
            setMessage("Не вдалося відправити лист ❌");
        } finally {
            setResending(false);
        }
    };

    return { resending, message, resend };
};
