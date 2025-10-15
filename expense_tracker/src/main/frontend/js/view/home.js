import {html} from "lit-html";
import page from "page";

export function showHome(ctx) {
    ctx.render(html`
        <section id="intro">
            <div class="logo">
                <div class="logo"><img src="/img/symbol-ring.svg" alt="Expensio symbol"/></div>
            </div>
            <h1><span class="brand">EXPEN</span><span class="highlight">SIO</span></h1>
            <p>
                Take control of your money with style. Expensio helps you track spending,
                crush bad habits, and turn every dollar into progress.<br/>Smart. Fast. Simple.
            </p>
            <button id="next-btn" class="next-btn">Next</button>
        </section>
    `);


    document.getElementById("next-btn").addEventListener("click", () =>{
        page.redirect("/login")
    })
}

