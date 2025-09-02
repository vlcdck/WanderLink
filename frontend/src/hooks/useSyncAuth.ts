import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { setTokens } from "../redux/slices/authSlice";

export const useSyncAuth = () => {
    const dispatch = useDispatch();
    useEffect(() => {
        const at = localStorage.getItem("accessToken");
        const rt = localStorage.getItem("refreshToken");
        if (at && rt) dispatch(setTokens({ accessToken: at, refreshToken: rt }));
    }, [dispatch]);
};
