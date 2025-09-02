import type {FC} from "react";

interface CheckEmailCardProps {
    resending: boolean;
    message: string | null;
    onResend: () => void;
}

const CheckEmailCard: FC<CheckEmailCardProps> = ({resending, message, onResend}) => {
    return (
        <div className="bg-white rounded-2xl shadow-xl p-10 max-w-md text-center">
            <div className="flex justify-center mb-6">
                <div className="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center text-4xl">
                    📩
                </div>
            </div>
            <h1 className="text-2xl font-bold mb-4">Перевірте свою пошту</h1>
            <p className="text-gray-600 mb-6">
                Ми надіслали листа з підтвердженням на вашу пошту.
            </p>

            <button
                onClick={onResend}
                disabled={resending}
                className="w-full border border-gray-300 py-3 rounded-xl font-semibold hover:bg-gray-100 transition"
            >
                {resending ? "Відправляємо..." : "Надіслати повторно"}
            </button>

            {message && <p className="text-sm text-gray-500 mt-2">{message}</p>}
        </div>
    );
};

export default CheckEmailCard;