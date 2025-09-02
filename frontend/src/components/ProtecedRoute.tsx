import { useSelector } from "react-redux";
import { Navigate } from "react-router";
import type { FC, ReactNode } from "react";
import type { RootState } from "../redux/store";

interface ProtectedRouteProps { children: ReactNode }

const ProtectedRoute: FC<ProtectedRouteProps> = ({ children }) => {
    const isAuth = useSelector((state: RootState) => state.auth.isAuthenticated);
    if (!isAuth) return <Navigate to="/auth" replace />;
    return <>{children}</>;
};

export default ProtectedRoute;
