import {createRoot} from 'react-dom/client'
import {Provider} from "react-redux";
import {RouterProvider} from "react-router";

import './index.css'
import {router} from "./router/router.tsx";
import {store} from "./redux/store.ts";
import SyncAuth from "./components/SyncAuth.tsx";


createRoot(document.getElementById("root")!).render(
    <Provider store={store}>
        <SyncAuth/>
        <RouterProvider router={router}/>
    </Provider>
);
