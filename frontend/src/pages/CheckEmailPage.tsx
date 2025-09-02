import type {FC} from "react"; // <-- type-only import
import CheckEmailCard from "../components/CheckEmailCard";
import {useResendEmail} from "../hooks/useResendEmail";

const CheckEmailPage: FC = () => {
    const {resending, message, resend} = useResendEmail();

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
            <CheckEmailCard resending={resending} message={message} onResend={resend}/>
        </div>
    );
};

export default CheckEmailPage;
