import type {FC} from "react";
import {useState} from "react";
import {useAuth} from "../hooks/useAuth.ts";
import type {AuthFormData} from "../types/AuthFormData.ts";

interface AuthFormProps {
    isRegister: boolean;
    toggleMode: () => void;
}

const AuthForm: FC<AuthFormProps> = ({isRegister, toggleMode}) => {
    const {register, login, loading, error, setError} = useAuth();

    const [form, setForm] = useState<AuthFormData>({
        email: "",
        password: "",
        username: "",
        firstName: "",
        lastName: "",
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setForm({...form, [e.target.name]: e.target.value});
        setError(null);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (isRegister) await register(form);
        else await login(form);
    };

    return (
        <form
            className="bg-white p-8 rounded-lg shadow-md w-full max-w-md"
            onSubmit={handleSubmit}
        >
            <h1 className="text-2xl font-bold mb-6 text-center">
                {isRegister ? "Реєстрація" : "Логін"}
            </h1>
            {error && <p className="text-red-500 mb-4">{error}</p>}
            {isRegister && (
                <>
                    <input
                        name="firstName"
                        placeholder="Ім'я"
                        value={form.firstName}
                        onChange={handleChange}
                        className="border p-2 mb-2 w-full rounded"
                    />
                    <input
                        name="lastName"
                        placeholder="Прізвище"
                        value={form.lastName}
                        onChange={handleChange}
                        className="border p-2 mb-2 w-full rounded"
                    />
                    <input
                        name="username"
                        placeholder="Username"
                        value={form.username}
                        onChange={handleChange}
                        className="border p-2 mb-2 w-full rounded"
                    />
                </>
            )}
            <input
                name="email"
                placeholder="Email"
                value={form.email}
                onChange={handleChange}
                className="border p-2 mb-2 w-full rounded"
            />
            <input
                name="password"
                type="password"
                placeholder="Пароль"
                value={form.password}
                onChange={handleChange}
                className="border p-2 mb-4 w-full rounded"
            />
            <button
                type="submit"
                disabled={loading}
                className="bg-blue-500 text-white w-full py-2 rounded mb-2 hover:bg-blue-600 transition disabled:opacity-50"
            >
                {loading
                    ? "Завантаження..."
                    : isRegister
                        ? "Зареєструватися"
                        : "Увійти"}
            </button>
            <p className="text-center text-gray-500 mt-2">
                {isRegister ? "Вже маєте акаунт?" : "Немає акаунта?"}{" "}
                <span
                    className="text-blue-500 cursor-pointer"
                    onClick={toggleMode}
                >
          {isRegister ? "Увійти" : "Зареєструватися"}
        </span>
            </p>
        </form>
    );
};

export default AuthForm;
