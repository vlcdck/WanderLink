import { useDispatch } from "react-redux";
import DashboardCard from "../components/DashboardCard";
import { clearTokens } from "../redux/slices/authSlice";

const DashboardPage = () => {
    const dispatch = useDispatch();
    const logout = () => {
        dispatch(clearTokens());
        window.location.href = "/auth";
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
            <DashboardCard onLogout={logout} />
        </div>
    );
};

export default DashboardPage;
