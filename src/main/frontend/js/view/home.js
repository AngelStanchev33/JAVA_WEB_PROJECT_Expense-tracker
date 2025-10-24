import {html} from "lit-html";
import page from "page";
import "../../public/css/home.css";

export function showHome(ctx) {
    // Set background image and remove blur
    document.body.style.backgroundImage = "url('/img/Expensio.png')";
    document.body.className = 'home-page';

    ctx.render(html`
        <section id="intro" class="container-fluid">
            <div class="row justify-content-center align-items-center min-vh-100">
                <div class="col-12 col-md-10 col-lg-8 text-center">
                    <div class="logo">
                        <img src="/img/symbol-ring.svg" class="img-fluid" alt="Expensio symbol"/>
                    </div>
                    <h1><span class="brand">EXPEN</span><span class="highlight">SIO</span></h1>
                    <p class="px-2">
                        Take control of your money with style. Expensio helps you track spending,
                        crush bad habits, and turn every dollar into progress.<br/>Smart. Fast. Simple.
                    </p>
                    <button id="next-btn" class="btn next-btn mt-2">Next</button>
                </div>
            </div>
        </section>
    `);


    document.getElementById("next-btn").addEventListener("click", () =>{
        page("/login")
    })
}

