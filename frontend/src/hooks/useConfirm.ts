import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router";
import { useDispatch } from "react-redux";
import api from "../api/api";
import { setTokens } from "../redux/slices/authSlice";

export const useConfirm = () => {
    const [status, setStatus] = useState<"loading" | "success" | "error">("loading");
    const [searchParams] = useSearchParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        const token = searchParams.get("token");
        if (!token) { setStatus("error"); return; }

        const finish = (accessToken: string, refreshToken: string) => {
            dispatch(setTokens({ accessToken, refreshToken }));

            if (window.opener) {
                try {
                    window.opener.postMessage({ type: "CONFIRM_ACCOUNT", payload: { accessToken, refreshToken } }, window.location.origin);
                    window.close();
                    return;
                } catch (err) {
                    console.error(err);
                }
            }

            navigate("/dashboard");
        };

        (async () => {
            try {
                const res = await api.get(`/auth/confirm?token=${token}`);
                finish(res.data.accessToken, res.data.refreshToken);
                setStatus("success");
            } catch {
                setStatus("error");
            }
        })();
    }, [dispatch, navigate, searchParams]);

    return { status };
};
