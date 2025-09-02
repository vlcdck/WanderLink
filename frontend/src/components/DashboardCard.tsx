import type {FC} from "react";

interface DashboardCardProps {
    onLogout: () => void;
}

const DashboardCard: FC<DashboardCardProps> = ({ onLogout }) => (
    <div className="p-6">
        <h1 className="text-xl font-bold">Привіт! 👋 Це дашборд</h1>
        <button
            onClick={onLogout}
            className="bg-red-500 text-white px-4 py-2 rounded mt-4"
        >
            Вийти
        </button>
    </div>
);

export default DashboardCard;