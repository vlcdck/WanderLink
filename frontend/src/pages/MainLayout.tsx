import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearUser, fetchProfile, selectUserProfile } from "../redux/slices/userSlice";
import { clearTokens } from "../redux/slices/authSlice";
import AccountSettingsModal from "../components/AccountSettingsModal";
import { useAppDispatch, useAppSelector } from "../redux/hooks/hooks";

const MainLayout = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    const profile = useAppSelector(selectUserProfile);

    const [isSettingsOpen, setIsSettingsOpen] = useState(false);

    useEffect(() => {
        dispatch(fetchProfile());
    }, [dispatch]);

    const handleLogout = () => {
        dispatch(clearTokens());
        dispatch(clearUser());
        navigate("/auth");
    };

    return (
        <div className="flex flex-col h-screen">
            {/* Header */}
            <header className="flex items-center justify-between px-6 py-3 bg-gray-800 text-white shadow-md">
                <h1 className="text-xl font-bold">WanderLink</h1>
                <div className="flex items-center gap-4">
                    {profile?.avatarUrl ? (
                        <img src={profile.avatarUrl} alt="avatar" className="w-10 h-10 rounded-full object-cover border" />
                    ) : (
                        <div className="w-10 h-10 rounded-full bg-gray-600 flex items-center justify-center text-white">
                            {profile?.username?.[0]?.toUpperCase() ?? "?"}
                        </div>
                    )}
                    <span>{profile?.username ?? profile?.email}</span>

                    <button className="px-3 py-1 bg-blue-600 rounded hover:bg-blue-700" onClick={() => setIsSettingsOpen(true)}>
                        Налаштування
                    </button>
                    <button className="px-3 py-1 bg-red-600 rounded hover:bg-red-700" onClick={handleLogout}>
                        Вийти
                    </button>
                </div>
            </header>

            {/* Main */}
            <div className="flex flex-1">
                <main className="flex-1 flex items-center justify-center bg-gray-200">
                    <span className="text-lg text-gray-600">[Тут буде карта Google Maps]</span>
                </main>
                <aside className="w-80 bg-white shadow-md p-4 border-l">
                    <h2 className="text-lg font-semibold mb-2">Мої подорожі</h2>
                    <div className="text-gray-500">[Тут буде список подорожей]</div>
                </aside>
            </div>

            <AccountSettingsModal open={isSettingsOpen} onClose={() => setIsSettingsOpen(false)} />
        </div>
    );
};

export default MainLayout;
