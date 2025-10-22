import {html} from "lit-html";
import page from "page";
import {register} from "../services/auth.js";

export function showRegister(ctx) {
    document.body.style.backgroundImage = "url('/img/Expensio.png')";
    document.body.className = "login-page register-page";

    ctx.render(html`
        <main class="container">
            <div class="row justify-content-center align-items-center min-vh-100">
                <div class="col-12 col-sm-10 col-md-8 col-lg-6 col-xl-15">
                    <header class="brand text-center mb-4">
                        <div class="logo">
                            <img src="/img/symbol-ring.svg" class="img-fluid" alt="Expensio symbol"/>
                        </div>
                        <h1><span class="brand">EXPEN</span><span class="highlight">SIO</span></h1>
                    </header>

                    <section class="card register-card">
                        <h2>Create account</h2>
                        <p class="lead mb-3">Join Expensio to track spending and stay in control.</p>

                        <form id="register-form" class="needs-validation" novalidate autocomplete="on">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email address</label>
                                <input
                                    type="email"
                                    class="form-control"
                                    id="email"
                                    name="email"
                                    required
                                    autocomplete="email"
                                    placeholder="you@example.com"
                                />
                            </div>

                            <div class="row g-3">
                                <div class="col-12 col-md-6">
                                    <label for="firstname" class="form-label">First name</label>
                                    <input
                                        type="text"
                                        class="form-control"
                                        id="firstname"
                                        name="firstname"
                                        required
                                        minlength="3"
                                        maxlength="20"
                                        autocomplete="given-name"
                                        placeholder="Ivan"
                                    />
                                </div>
                                <div class="col-12 col-md-6">
                                    <label for="lastname" class="form-label">Last name</label>
                                    <input
                                        type="text"
                                        class="form-control"
                                        id="lastname"
                                        name="lastname"
                                        required
                                        minlength="3"
                                        maxlength="20"
                                        autocomplete="family-name"
                                        placeholder="Petrov"
                                    />
                                </div>
                            </div>

                            <div class="row g-3">
                                <div class="col-12 col-md-6">
                                    <label for="password" class="form-label">Password</label>
                                    <input
                                        type="password"
                                        class="form-control"
                                        id="password"
                                        name="password"
                                        required
                                        minlength="8"
                                        autocomplete="new-password"
                                    />
                                    <small class="form-text text-muted">At least 8 characters.</small>
                                </div>

                                <div class="col-12 col-md-6">
                                    <label for="confirmPassword" class="form-label">Confirm password</label>
                                    <input
                                        type="password"
                                        class="form-control"
                                        id="confirmPassword"
                                        name="confirmPassword"
                                        required
                                        minlength="8"
                                        autocomplete="new-password"
                                    />
                                    <small class="form-text text-muted">Must match password.</small>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary w-100">Sign up</button>
                        </form>

                        <div id="error-message" class="alert alert-danger mt-3" style="display: none;" role="alert"></div>

                        <p class="alt text-center mt-3">
                            Already have an account? <a href="/login" id="signin-link">Log in</a>
                        </p>
                    </section>
                </div>
            </div>
        </main>
    `);

    // ====== EVENT LISTENER LOGIC ======
    const registerForm = document.getElementById("register-form");
    const errorMessage = document.getElementById("error-message");
    const submitButton = registerForm?.querySelector('button[type="submit"]');

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            // Clear previous errors
            if (errorMessage) {
                errorMessage.style.display = "none";
                errorMessage.textContent = "";
            }

            // Gather field values (trim whitespace)
            const email = document.getElementById("email").value.trim();
            const firstname = document.getElementById("firstname").value.trim();
            const lastname = document.getElementById("lastname").value.trim();
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword").value;

            // Client-side validation
            if (!email || !firstname || !lastname || !password) {
                errorMessage.textContent = "All fields are required";
                errorMessage.style.display = "block";
                return;
            }

            if (password.length < 8) {
                errorMessage.textContent = "Password must be at least 8 characters";
                errorMessage.style.display = "block";
                return;
            }

            if (password !== confirmPassword) {
                errorMessage.textContent = "Passwords do not match";
                errorMessage.style.display = "block";
                return;
            }

            // Disable submit button during async call
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.textContent = "Creating account...";
            }

            try {
                // Call register service
                await register({ email, firstname, lastname, password, confirmPassword });

                // Redirect using ctx for testability
                ctx.page.redirect("/");
            } catch (error) {
                // Show inline error (no alert!)
                if (errorMessage) {
                    errorMessage.textContent = error.message;
                    errorMessage.style.display = "block";
                }
            } finally {
                // Re-enable button
                if (submitButton) {
                    submitButton.disabled = false;
                    submitButton.textContent = "Sign up";
                }
            }
        });
    }
}
