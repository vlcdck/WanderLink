import {createBrowserRouter, Navigate} from "react-router";
import AuthPage from "../pages/AuthPage.tsx";
import ConfirmPage from "../pages/ConfirmPage.tsx";
import DashboardPage from "../pages/DashboardPage.tsx";
import {authStorage} from "../lib/authStorage.ts";
import ProtectedRoute from "../components/ProtecedRoute.tsx";
import CheckEmailPage from "../pages/CheckEmailPage.tsx";
import OAuth2RedirectHandler from "../pages/OAuth2RedirectHandler.tsx";

export const router = createBrowserRouter([
    {path: "/", element: <Navigate to="/dashboard"/>},
    {path: "/auth", element: authStorage.getAccessToken() ? <Navigate to="/dashboard"/> : <AuthPage/>},
    {path: "/check-email", element: <CheckEmailPage/>},
    {path: "/confirm", element: <ConfirmPage/>},
    {path: "/oauth2/redirect", element: <OAuth2RedirectHandler/>},
    {path: "/dashboard", element: <ProtectedRoute><DashboardPage/></ProtectedRoute>},
]);