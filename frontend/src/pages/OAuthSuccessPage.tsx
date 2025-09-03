import {useDispatch} from "react-redux";
import {useNavigate, useSearchParams} from "react-router";
import {useEffect} from "react";
import {setTokens} from "../redux/slices/authSlice.ts";

const OAuthSuccessPage = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    useEffect(() => {
        const accessToken = searchParams.get("accessToken");
        const refreshToken = searchParams.get("refreshToken");

        if (accessToken && refreshToken) {
            dispatch(setTokens({ accessToken, refreshToken }));
            navigate("/dashboard", { replace: true });
        } else {
            navigate("/auth", { replace: true });
        }
    }, [dispatch, navigate, searchParams]);

    return (
        <div className="flex items-center justify-center h-screen">
        <p className="text-lg">Зачекайте, виконується вхід через Google...</p>
    </div>
);
};

export default OAuthSuccessPage;