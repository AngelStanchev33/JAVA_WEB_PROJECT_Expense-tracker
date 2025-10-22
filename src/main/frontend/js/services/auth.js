import { baseUrl, handleResponse } from "../utils/http.js";

// Re-export createAuthHeaders for convenience
export { createAuthHeaders } from "../utils/http.js";

// ===== Token Management Helpers =====

/**
 * Decode base64url string to regular base64, then decode with atob()
 * JWT uses base64url encoding (RFC 4648): replaces + with -, / with _, no padding
 */
function base64urlDecode(str) {
    // Convert base64url to base64
    let base64 = str.replace(/-/g, '+').replace(/_/g, '/');

    // Add padding if needed (length must be multiple of 4)
    const pad = base64.length % 4;
    if (pad) {
        if (pad === 1) {
            throw new Error('Invalid base64url string');
        }
        base64 += '='.repeat(4 - pad);
    }

    return atob(base64);
}

export function setToken(token) {
    if (token) {
        localStorage.setItem("jwt_token", token);
    }
}

export function getToken() {
    const token = localStorage.getItem("jwt_token");

    if (!token) {
        return null;
    }

    // Decode JWT and check expiration
    try {
        // JWT format: header.payload.signature
        // Use base64url decode instead of atob directly
        const payloadBase64url = token.split('.')[1];
        const payloadJson = base64urlDecode(payloadBase64url);
        const payload = JSON.parse(payloadJson);

        const exp = payload.exp * 1000; // Convert to milliseconds

        // Check if expired
        if (Date.now() >= exp) {
            console.warn("Token expired, logging out");
            logout();
            return null;
        }

        return token;
    } catch (error) {
        console.error("Invalid JWT token format", error);
        logout();
        return null;
    }
}

export function isAuthenticated() {
    return !!getToken(); // getToken() already validates token
}

export function logout(onLogoutCallback) {
    // Clear JWT token
    localStorage.removeItem("jwt_token");

    // Future: clear refresh token if implemented
    // localStorage.removeItem("refresh_token");

    // Call optional callback (e.g., redirect to login)
    if (typeof onLogoutCallback === 'function') {
        onLogoutCallback();
    }
}

// ===== Auth API Functions =====

export async function login(username, password) {
    const response = await fetch(`${baseUrl}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
    });

    const result = await handleResponse(response); // Use centralized handler

    setToken(result.token); // Use setToken helper
    return result; // Return full response (may include user info)
}

export async function register({ email, firstname, lastname, password, confirmPassword }) {
    const registerResponse = await fetch(`${baseUrl}/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, firstname, lastname, password, confirmPassword }),
    });

    await handleResponse(registerResponse, false); // expectBody = false

    return await login(email, password); // Return token and any user data
}
