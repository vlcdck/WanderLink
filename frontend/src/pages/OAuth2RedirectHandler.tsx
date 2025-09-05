import {useNavigate, useSearchParams} from "react-router";
import {useEffect} from "react";
import {useDispatch} from "react-redux";
import {setTokens} from "../redux/slices/authSlice.ts";

const OAuth2RedirectHandler = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        const accessToken = searchParams.get("accessToken");
        const refreshToken = searchParams.get("refreshToken");

        if (accessToken && refreshToken) {
            // зберігаємо токени
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("refreshToken", refreshToken);
            dispatch(setTokens({accessToken, refreshToken}));

            navigate("/dashboard");
        } else {
            navigate("/auth");
        }
    }, [dispatch, navigate, searchParams]);

    return <p>Авторизація через Google...</p>;
};

export default OAuth2RedirectHandler;