import page from "page";
import {render} from "lit-html";
import {showDashboard} from "./view/dashboard.js";
import {showLogin} from "./view/login.js";
import {showRegister} from "./view/register.js";
import {isAuthenticated} from "./services/auth.js";

const root = document.querySelector("main");

function decorateContent(ctx, next) {
    if (!root) {
        console.error("Main element not found!");
        return;
    }
    ctx.render = (content) => render(content, root);
    next();
}

function requireAuth(ctx, next) {
    if (isAuthenticated()) {
        next();
    } else {
        page("/login");
    }
}

page(decorateContent)
page("/", requireAuth, showDashboard);
page("/login", showLogin);
page("/register", showRegister);
page.start();

