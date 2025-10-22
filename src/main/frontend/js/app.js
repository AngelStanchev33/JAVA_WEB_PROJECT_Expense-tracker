import page from "page";
import {render} from "lit-html";
import {showHome} from "./view/home.js";
import {showLogin} from "./view/login.js";
import {showRegister} from "./view/register.js";

const root = document.querySelector("main");

// Set global unauthorized handler
window.onUnauthorized = () => {
    console.warn("User session expired, redirecting to login");
    page.redirect("/login");
};

function decorateContent(ctx, next) {
    if (!root) {
        console.error("Main element not found!");
        return;
    }
    ctx.render = (content) => render(content, root);
    next();
}

page(decorateContent)
page("/", showHome);
page("/login", showLogin);
page("/register", showRegister);
page.start();

