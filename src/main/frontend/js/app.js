import page from "page";
import {render} from "lit-html";
import {showHome} from "./view/home.js";
import {showLogin} from "./view/login.js";
import {showRegister} from "./view/register.js";

const root = document.querySelector("main");

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

