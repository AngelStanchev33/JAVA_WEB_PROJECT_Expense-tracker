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