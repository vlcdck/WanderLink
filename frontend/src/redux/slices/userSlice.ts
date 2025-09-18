// src/redux/slices/userSlice.ts
import {createAsyncThunk, createSlice} from "@reduxjs/toolkit";
import api from "../../api/api";
import type {RootState} from "../store";

export type ExperienceLevel = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";

export interface UserProfile {
    id: number;
    email: string;
    username?: string;
    firstName?: string;
    lastName?: string;
    avatarUrl?: string;
    bio?: string;
    experienceLevel?: ExperienceLevel;
    weight?: number;
    height?: number;
    hasMedicalConditions?: boolean;
    medicalNotes?: string;
    phoneNumber?: string;
}

export interface UserState {
    profile: UserProfile | null;
    loading: boolean;
    error: string | null;
    updating: boolean;
    avatarUploading: boolean;
}

const initialState: UserState = {
    profile: null,
    loading: false,
    error: null,
    updating: false,
    avatarUploading: false,
};

// Отримати профіль
export const fetchProfile = createAsyncThunk<UserProfile, void, { rejectValue: string }>(
    "user/fetchProfile",
    async (_, {rejectWithValue}) => {
        try {
            const res = await api.get("/profile/me");
            return res.data as UserProfile;
        } catch (err: any) {
            return rejectWithValue(err?.response?.data?.message || "Не вдалося отримати профіль");
        }
    }
);

// Оновлення профілю
export const updateProfile = createAsyncThunk<UserProfile, Partial<UserProfile>, { rejectValue: string }>(
    "user/updateProfile",
    async (payload, { rejectWithValue }) => {
        try {
            const res = await api.patch("/profile/me", payload);
            return res.data as UserProfile; // повертаємо новий профіль
        } catch (err: any) {
            return rejectWithValue(err?.response?.data?.message || "Помилка оновлення профілю");
        }
    }
);

// Оновлення аватара
export const updateAvatar = createAsyncThunk<UserProfile, File, { rejectValue: string }>(
    "user/updateAvatar",
    async (file, { rejectWithValue }) => {
        try {
            const fd = new FormData();
            fd.append("avatar", file);
            const res = await api.patch("/profile/me/avatar", fd, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            return res.data as UserProfile; // повертаємо оновлений профіль
        } catch (err: any) {
            return rejectWithValue(err?.response?.data?.message || "Помилка завантаження аватару");
        }
    }
);

// Видалення аватара
export const deleteAvatar = createAsyncThunk<UserProfile, void, { rejectValue: string }>(
    "user/deleteAvatar",
    async (_, { rejectWithValue }) => {
        try {
            const res = await api.delete("/profile/me/avatar");
            return res.data as UserProfile; // повертаємо оновлений профіль
        } catch (err: any) {
            return rejectWithValue(err?.response?.data?.message || "Не вдалося видалити аватар");
        }
    }
);

// Зміна пароля
export const changePassword = createAsyncThunk<void, { oldPassword: string; newPassword: string }, { rejectValue: string }>(
    "user/changePassword",
    async (payload, {rejectWithValue}) => {
        try {
            await api.patch("/profile/me/password", payload);
        } catch (err: any) {
            return rejectWithValue(err?.response?.data?.message || "Не вдалося змінити пароль");
        }
    }
);

const userSlice = createSlice({
    name: "user",
    initialState,
    reducers: {
        clearUser: (state) => {
            state.profile = null;
            state.error = null;
            state.loading = false;
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchProfile.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchProfile.fulfilled, (state, action) => {
                state.loading = false;
                state.profile = action.payload;
            })
            .addCase(fetchProfile.rejected, (state, action) => {
                state.loading = false;
                state.error = action.payload || "Помилка завантаження профілю";
            })

            .addCase(updateProfile.pending, (state) => {
                state.updating = true;
                state.error = null;
            })
            .addCase(updateProfile.fulfilled, (state, action) => {
                state.updating = false;
                state.profile = action.payload;
            })
            .addCase(updateProfile.rejected, (state, action) => {
                state.updating = false;
                state.error = action.payload || "Помилка оновлення профілю";
            })

            .addCase(updateAvatar.pending, (state) => {
                state.avatarUploading = true;
                state.error = null;
            })
            .addCase(updateAvatar.fulfilled, (state, action) => {
                state.avatarUploading = false;
                state.profile = action.payload;
            }).addCase(updateAvatar.rejected, (state, action) => {
            state.avatarUploading = false;
            state.error = action.payload || "Помилка оновлення аватару";
        })

            .addCase(deleteAvatar.fulfilled, (state, action) => {
                state.profile = action.payload;
            })
            .addCase(changePassword.rejected, (state, action) => {
                state.error = action.payload || "Помилка зміни пароля";
            });
    },
});

export const {clearUser} = userSlice.actions;
export default userSlice.reducer;

// Селектори
export const selectUserProfile = (state: RootState) => state.user.profile;
export const selectUserLoading = (state: RootState) => state.user.loading;
export const selectUserError = (state: RootState) => state.user.error;
