import type {FC} from "react";

interface ConfirmCardProps {
    status: "loading" | "success" | "error";
}

const ConfirmCard: FC<ConfirmCardProps> = ({status}) => {
    if (status === "loading") return <p>Підтвердження...</p>;
    if (status === "error") return <p className="text-red-500">❌ Лінк недійсний або прострочений</p>;
    return <p className="text-green-500">✅ Пошта підтверджена! Можете закрити цю вкладку.</p>;
};

export default ConfirmCard;