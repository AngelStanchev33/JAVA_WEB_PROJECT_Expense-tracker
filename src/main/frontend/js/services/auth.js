const baseUrl = "http://localhost:8080/api"

export async function login(username, password){
    const response = await fetch(`${baseUrl}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({username, password}),
    });

    const result = await response.json();

    if (!response.ok) {
        throw new Error(result.error || "Login failed");
    }

    localStorage.setItem("jwt_token", result.token);
    return result.token;
}

export function isAuthenticated() {
    const token = localStorage.getItem("jwt_token");
    return token !== null && token !== undefined && token !== "";
}

export function logout() {
    localStorage.removeItem("jwt_token");
}

export async function getCurrentUser() {
    const token = localStorage.getItem("jwt_token");

    if (!token) {
        throw new Error("No authentication token found");
    }

    const response = await fetch(`${baseUrl}/user/me`, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    });

    if (!response.ok) {
        throw new Error("Failed to fetch user information");
    }

    return await response.json();
}