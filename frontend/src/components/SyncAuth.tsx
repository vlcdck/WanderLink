import type {FC} from "react";
import {useSyncAuth} from "../hooks/useSyncAuth";

const SyncAuth: FC = () => {
    useSyncAuth();
    return null;
};

export default SyncAuth;
