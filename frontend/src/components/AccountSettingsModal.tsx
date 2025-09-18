import { type ChangeEvent, useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "../redux/hooks/hooks";
import {
    changePassword,
    deleteAvatar,
    type ExperienceLevel,
    selectUserProfile,
    updateAvatar,
    updateProfile,
} from "../redux/slices/userSlice";

interface Props {
    open: boolean;
    onClose: () => void;
}

export default function AccountSettingsModal({ open, onClose }: Props) {
    const dispatch = useAppDispatch();
    const profile = useAppSelector(selectUserProfile);

    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        username: "",
        bio: "",
        experienceLevel: "BEGINNER" as ExperienceLevel,
        weight: "",
        height: "",
        hasMedicalConditions: false,
        medicalNotes: "",
        phoneNumber: "",
    });

    const [avatarFile, setAvatarFile] = useState<File | null>(null);
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");

    useEffect(() => {
        if (profile) {
            setForm({
                firstName: profile.firstName || "",
                lastName: profile.lastName || "",
                username: profile.username || "",
                bio: profile.bio || "",
                experienceLevel: profile.experienceLevel || "BEGINNER",
                weight: profile.weight?.toString() || "",
                height: profile.height?.toString() || "",
                hasMedicalConditions: profile.hasMedicalConditions || false,
                medicalNotes: profile.medicalNotes || "",
                phoneNumber: profile.phoneNumber || "",
            });
        }
    }, [profile]);

    if (!open) return null;

    const handleInputChange = (
        e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
    ) => {
        const { name, value, type } = e.target;

        setForm((prev) => ({
            ...prev,
            [name]:
                type === "checkbox"
                    ? (e.target as HTMLInputElement).checked
                    : value,
        }));
    };

    const handleUpdateProfile = () => {
        const payload: any = {
            ...form,
            weight: form.weight ? Number(form.weight) : null,
            height: form.height ? Number(form.height) : null,
        };

        // Якщо галочка не стоїть – не відправляємо medicalNotes
        if (!form.hasMedicalConditions) {
            delete payload.medicalNotes;
        }

        dispatch(updateProfile(payload));
    };

    const handleAvatarChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files?.[0]) {
            setAvatarFile(e.target.files[0]); // тільки вибираємо файл
        }
    };

    const handleSaveAvatar = () => {
        if (avatarFile) {
            dispatch(updateAvatar(avatarFile));
            setAvatarFile(null); // очищаємо після завантаження
        }
    };

    const handleDeleteAvatar = () => dispatch(deleteAvatar());

    const handleChangePassword = () => {
        if (!oldPassword || !newPassword) return;
        dispatch(changePassword({ oldPassword, newPassword }));
        setOldPassword("");
        setNewPassword("");
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50 overflow-auto p-4">
            <div className="bg-white rounded-lg shadow-lg w-full max-w-2xl p-6">
                <h2 className="text-xl font-bold mb-4">Налаштування акаунту</h2>

                {/* Profile */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                    <input
                        name="firstName"
                        type="text"
                        placeholder="Ім’я"
                        value={form.firstName}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                    />
                    <input
                        name="lastName"
                        type="text"
                        placeholder="Прізвище"
                        value={form.lastName}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                    />
                    <input
                        name="username"
                        type="text"
                        placeholder="Нікнейм"
                        value={form.username}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                    />
                    <input
                        name="phoneNumber"
                        type="text"
                        placeholder="Телефон"
                        value={form.phoneNumber}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                    />
                    <textarea
                        name="bio"
                        placeholder="Біо"
                        value={form.bio}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2 md:col-span-2"
                    />
                    <select
                        name="experienceLevel"
                        value={form.experienceLevel}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                    >
                        <option value="BEGINNER">BEGINNER</option>
                        <option value="INTERMEDIATE">INTERMEDIATE</option>
                        <option value="ADVANCED">ADVANCED</option>
                    </select>
                    <input
                        name="weight"
                        type="number"
                        placeholder="Вага (кг)"
                        value={form.weight}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                    />
                    <input
                        name="height"
                        type="number"
                        placeholder="Зріст (см)"
                        value={form.height}
                        onChange={handleInputChange}
                        className="w-full border rounded px-3 py-2"
                    />
                    <div className="flex items-center gap-2">
                        <input
                            name="hasMedicalConditions"
                            type="checkbox"
                            checked={form.hasMedicalConditions}
                            onChange={handleInputChange}
                        />
                        <label>Має медичні проблеми</label>
                    </div>

                    {form.hasMedicalConditions && (
                        <textarea
                            name="medicalNotes"
                            placeholder="Медичні нотатки"
                            value={form.medicalNotes}
                            onChange={handleInputChange}
                            className="w-full border rounded px-3 py-2 md:col-span-2"
                        />
                    )}
                </div>

                <button
                    onClick={handleUpdateProfile}
                    className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 mb-4"
                >
                    Зберегти профіль
                </button>

                {/* Avatar */}
                <div className="space-y-2 mb-4">
                    <input
                        type="file"
                        accept="image/*"
                        onChange={handleAvatarChange}
                    />
                    {avatarFile && (
                        <button
                            onClick={handleSaveAvatar}
                            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                        >
                            Зберегти аватар
                        </button>
                    )}
                    <button
                        onClick={handleDeleteAvatar}
                        className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
                    >
                        Видалити аватар
                    </button>
                </div>

                {/* Password */}
                <div className="space-y-3 mb-4">
                    <input
                        type="password"
                        placeholder="Старий пароль"
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                    />
                    <input
                        type="password"
                        placeholder="Новий пароль"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                    />
                    <button
                        onClick={handleChangePassword}
                        className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                    >
                        Змінити пароль
                    </button>
                </div>

                <div className="flex justify-end">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                    >
                        Закрити
                    </button>
                </div>
            </div>
        </div>
    );
}
