import page from "page";
import {render} from "lit-html";
import {showHome} from "./view/home.js";


const root = document.querySelector("main")

function decorateContent(ctx, next) {
    ctx.render = (content) => render(content, root);
    next();
}


page(decorateContent)
page("/", showHome)

page.start();


