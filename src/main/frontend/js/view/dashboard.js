import {html} from "lit-html";
import page from "page";
import {logout, getCurrentUser} from "../services/auth.js";
import "../../public/css/dashboard.css";

export async function showDashboard(ctx) {
    // Remove background image for dashboard
    document.body.style.backgroundImage = "none";
    document.body.className = 'dashboard-page';

    // Fetch current user information
    let user = null;
    try {
        user = await getCurrentUser();
    } catch (error) {
        console.error("Failed to fetch user:", error);
        // Redirect to login if user fetch fails
        page("/login");
        return;
    }

    // Get user initials for avatar
    const initials = user.firstname && user.lastname
        ? `${user.firstname[0]}${user.lastname[0]}`.toUpperCase()
        : user.email[0].toUpperCase();

    const fullName = `${user.firstname} ${user.lastname}`;

    ctx.render(html`
        <aside class="sidebar">
            <div class="user-profile-card">
                <div class="user-avatar">
                    ${user.imageUrl
                        ? html`<img src="${user.imageUrl}" alt="${fullName}" />`
                        : html`<div class="avatar-initials">${initials}</div>`
                    }
                </div>
                <div class="user-info">
                    <div class="user-name">${fullName}</div>
                    <div class="user-email">${user.email}</div>
                </div>
            </div>
            <nav class="nav">
                <a class="active" href="/">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M3 10.5l9-7 9 7"/>
                        <path d="M5 9.5V20h14V9.5"/>
                        <path d="M10 20v-6h4v6"/>
                    </svg>
                    <span>Home</span>
                </a>
                <a href="/expenses">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="3" y="4" width="18" height="14" rx="2"/>
                        <path d="M7 8h10"/>
                        <path d="M7 12h5"/>
                    </svg>
                    <span>Expenses</span>
                </a>
                <a href="/budgets">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M4 17V7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v10"/>
                        <path d="M8 13h8"/>
                        <path d="M8 9h8"/>
                    </svg>
                    <span>Budgets</span>
                </a>
                <a href="/settings">
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                        <line x1="2" y1="5" x2="8" y2="5"/>
                        <circle cx="11" cy="5" r="2.5" fill="none"/>
                        <line x1="14" y1="5" x2="22" y2="5"/>
                        <line x1="2" y1="12" x2="12" y2="12"/>
                        <circle cx="15" cy="12" r="2.5" fill="none"/>
                        <line x1="18" y1="12" x2="22" y2="12"/>
                        <line x1="2" y1="19" x2="6" y2="19"/>
                        <circle cx="9" cy="19" r="2.5" fill="none"/>
                        <line x1="12" y1="19" x2="22" y2="19"/>
                    </svg>
                    <span>Settings</span>
                </a>
                <a href="/support" class="support">
                    <svg class="ico-fill" viewBox="0 0 122.88 122.27" aria-hidden="true">
                        <path d="M33.84,50.25c4.13,7.45,8.89,14.6,15.07,21.12c6.2,6.56,13.91,12.53,23.89,17.63c0.74,0.36,1.44,0.36,2.07,0.11 c0.95-0.36,1.92-1.15,2.87-2.1c0.74-0.74,1.66-1.92,2.62-3.21c3.84-5.05,8.59-11.32,15.3-8.18c0.15,0.07,0.26,0.15,0.41,0.21 l22.38,12.87c0.07,0.04,0.15,0.11,0.21,0.15c2.95,2.03,4.17,5.16,4.2,8.71c0,3.61-1.33,7.67-3.28,11.1 c-2.58,4.53-6.38,7.53-10.76,9.51c-4.17,1.92-8.81,2.95-13.27,3.61c-7,1.03-13.56,0.37-20.27-1.69 c-6.56-2.03-13.17-5.38-20.39-9.84l-0.53-0.34c-3.31-2.07-6.89-4.28-10.4-6.89C31.12,93.32,18.03,79.31,9.5,63.89 C2.35,50.95-1.55,36.98,0.58,23.67c1.18-7.3,4.31-13.94,9.77-18.32c4.76-3.84,11.17-5.94,19.47-5.2c0.95,0.07,1.8,0.62,2.25,1.44 l14.35,24.26c2.1,2.72,2.36,5.42,1.21,8.12c-0.95,2.21-2.87,4.25-5.49,6.15c-0.77,0.66-1.69,1.33-2.66,2.03 c-3.21,2.33-6.86,5.02-5.61,8.18L33.84,50.25L33.84,50.25L33.84,50.25z"/>
                    </svg>
                    <span>Support</span>
                </a>
            </nav>
            <div class="brand">
                <div class="brand-mark"><img src="/img/symbol-ring.svg" alt="Expensio"/></div>
                <h1><span class="brand">EXPEN</span><span class="highlight">SIO</span></h1>
            </div>
        </aside>
        <div class="dashboard-content">
            <div class="dashboard-container">
                <div class="header">
                    <div class="logo"><i></i><span>Dashboard</span></div>
                    <div class="badge" style="padding:4px 8px;border:1px solid #2b3342;border-radius:999px;background:#12161e;color:#b7c0ce;cursor:pointer;" id="logout-btn">Logout</div>
                </div>
                <div class="grid">
                    <section class="card">
                        <div class="card-header">
                            Notifications
                            <span style="color:#a2adb9;font-weight:500">Overview</span>
                        </div>
                        <div class="card-body list">
                            <div class="list-item"><div>Pending Approvals</div><div><strong>5</strong></div></div>
                            <div class="list-item"><div>New Trips Registered</div><div><strong>1</strong></div></div>
                            <div class="list-item"><div>Unreported Expenses</div><div><strong>4</strong></div></div>
                            <div class="list-item"><div>Upcoming Expenses</div><div><strong>0</strong></div></div>
                            <div class="list-item"><div>Unreported Advances</div><div><strong>€0.00</strong></div></div>
                        </div>
                    </section>
                    <section class="card">
                        <div class="card-header">
                            Recent Expenses
                            <span style="color:#a2adb9;font-weight:500">Last 7 days</span>
                        </div>
                        <div class="card-body">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Subject</th>
                                        <th>Employee</th>
                                        <th>Team</th>
                                        <th style="text-align:right">Amount</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Office Supplies</td>
                                        <td>John Smith</td>
                                        <td><span class="badge tag-purple">Marketing</span></td>
                                        <td style="text-align:right" class="amount">€150.00</td>
                                    </tr>
                                    <tr>
                                        <td>Business Lunch</td>
                                        <td>Sarah Jade</td>
                                        <td><span class="badge tag-pink">Sales</span></td>
                                        <td style="text-align:right" class="amount">€75.50</td>
                                    </tr>
                                    <tr>
                                        <td>Travel Expenses</td>
                                        <td>Mike Brown</td>
                                        <td><span class="badge">Operations</span></td>
                                        <td style="text-align:right" class="amount">€450.25</td>
                                    </tr>
                                    <tr>
                                        <td>Client Dinner</td>
                                        <td>Jennifer Lee</td>
                                        <td><span class="badge tag-purple">Marketing</span></td>
                                        <td style="text-align:right" class="amount">€120.00</td>
                                    </tr>
                                    <tr>
                                        <td>Hotel</td>
                                        <td>David Wilson</td>
                                        <td><span class="badge tag-teal">Finance</span></td>
                                        <td style="text-align:right" class="amount">€275.75</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
                <section class="card" style="margin-top:16px">
                    <div class="card-header">Quick Access</div>
                    <div class="card-body actions">
                        <div class="action" id="new-expense-btn">
                            <div class="icon">➕</div>
                            <div>
                                <div style="font-weight:600">New expense</div>
                                <small class="muted">Add a single entry</small>
                            </div>
                        </div>
                        <div class="action" id="create-budget-btn">
                            <div class="icon">💼</div>
                            <div>
                                <div style="font-weight:600">Create budget</div>
                                <small>Set monthly limit</small>
                            </div>
                        </div>
                    </div>
                </section>
                <section class="card" style="margin-top:16px">
                    <div class="card-header">Monthly Report</div>
                    <div class="card-body charts">
                        <div>
                            <div style="margin-bottom:8px;font-weight:600">Team Spending Trend</div>
                            <div class="bars">
                                <div class="bar" style="height:48%"></div>
                                <div class="bar p2" style="height:26%"></div>
                                <div class="bar p3" style="height:74%"></div>
                                <div class="bar p4" style="height:62%"></div>
                                <div class="bar" style="height:88%"></div>
                                <div class="bar p2" style="height:34%"></div>
                            </div>
                            <div class="legend"><span>Q1–Q2</span><span>fictional data</span></div>
                        </div>
                        <div>
                            <div style="margin-bottom:8px;font-weight:600">Day‑to‑Day Expenses</div>
                            <div class="bars">
                                <div class="bar" style="height:22%"></div>
                                <div class="bar p2" style="height:56%"></div>
                                <div class="bar p3" style="height:78%"></div>
                                <div class="bar" style="height:64%"></div>
                                <div class="bar p2" style="height:30%"></div>
                                <div class="bar p3" style="height:42%"></div>
                            </div>
                            <div class="legend">
                                <span>Accommodation</span>
                                <span>Comms</span>
                                <span>Services</span>
                                <span>Food</span>
                                <span>Fuel</span>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    `);

    // Event listener for logout button
    const logoutBtn = document.getElementById("logout-btn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            logout();
            page("/login");
        });
    }

    // Event listener for new expense button
    const newExpenseBtn = document.getElementById("new-expense-btn");
    if (newExpenseBtn) {
        newExpenseBtn.addEventListener("click", () => {
            alert("New expense feature coming soon!");
        });
    }

    // Event listener for create budget button
    const createBudgetBtn = document.getElementById("create-budget-btn");
    if (createBudgetBtn) {
        createBudgetBtn.addEventListener("click", () => {
            alert("Create budget feature coming soon!");
        });
    }
}
