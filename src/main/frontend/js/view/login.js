import {html} from "lit-html";
import page from "page";
import {login} from "../services/auth.js";

export function showLogin(ctx) {
    // Set background image and add blur class
    document.body.style.backgroundImage = "url('/img/Expensio.png')";
    document.body.className = 'login-page';

    ctx.render(html`
        <main class="container">
            <div class="row justify-content-center align-items-center min-vh-100">
                <div class="col-12 col-sm-10 col-md-8 col-lg-5 col-xl-4">
                    <header class="brand text-center mb-4">
                        <div class="logo">
                            <img src="/img/symbol-ring.svg" class="img-fluid" alt="Expensio symbol"/>
                        </div>
                        <h1><span class="brand">EXPEN</span><span class="highlight">SIO</span></h1>
                    </header>

                    <section class="card">
                        <h2>Log in</h2>
                        <form id="login-form" autocomplete="on" class="needs-validation" novalidate>
                            <div class="mb-3">
                                <label for="username" class="form-label">Username</label>
                                <input
                                        type="text"
                                        class="form-control"
                                        id="username"
                                        name="username"
                                        required
                                        autocomplete="username"
                                />
                            </div>

                            <div class="mb-3">
                                <label for="password" class="form-label">Password</label>
                                <input
                                        type="password"
                                        class="form-control"
                                        id="password"
                                        name="password"
                                        required
                                        autocomplete="current-password"
                                />
                            </div>

                            <div id="error-message" class="text-danger text-center mt-2" style="display: none;"></div>

                            <button type="submit" class="btn btn-primary w-100">Continue</button>
                        </form>

                        <p class="alt text-center mt-3">
                            Don't have an account? <a href="/register" id="signup-link">Sign up</a>
                        </p>
                    </section>
                </div>
            </div>
        </main>
    `);

    // Event listener for Sign up link
    const signupLink = document.getElementById("signup-link");
    if (signupLink) {
        signupLink.addEventListener("click", (e) => {
            e.preventDefault();
            page.redirect("/register");
        });
    }

    const loginForm = document.getElementById("login-form");
    const errorMessage = document.getElementById("error-message");
    const submitButton = loginForm?.querySelector('button[type="submit"]');

    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            // Clear previous error
            if (errorMessage) {
                errorMessage.style.display = "none";
                errorMessage.textContent = "";
            }

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;

            // Disable button and show loading state
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.textContent = "Loading...";
            }

            try {
                const token = await login(username, password);
                ctx.page.redirect("/");
            } catch (error) {
                // Show error message
                if (errorMessage) {
                    errorMessage.textContent = error.message;
                    errorMessage.style.display = "block";
                }
            } finally {
                // Re-enable button
                if (submitButton) {
                    submitButton.disabled = false;
                    submitButton.textContent = "Continue";
                }
            }
        })
    }
}