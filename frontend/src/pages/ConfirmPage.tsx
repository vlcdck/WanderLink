import type {FC} from "react"; // <-- type-only import
import ConfirmCard from "../components/ConfirmCard";
import {useConfirm} from "../hooks/useConfirm";

const ConfirmPage: FC = () => {
    const {status} = useConfirm();
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
            <ConfirmCard status={status}/>
        </div>
    );
};

export default ConfirmPage;
