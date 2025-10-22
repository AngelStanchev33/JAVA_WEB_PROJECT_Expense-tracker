import { baseUrl } from "../config/api.js";
import { getToken, logout } from "../services/auth.js";

// Re-export baseUrl for convenience
export { baseUrl };

/**
 * Centralized response handler for all fetch requests
 * Handles 401 errors, empty responses, and error parsing
 */
export async function handleResponse(response, expectBody = true) {
    // Handle 401 Unauthorized - expired/invalid token
    if (response.status === 401) {
        // Call global unauthorized handler if exists
        if (window.onUnauthorized) {
            window.onUnauthorized();
        }
        // Auto logout
        logout();
        throw new Error("Unauthorized - please login again");
    }

    // Handle other errors
    if (!response.ok) {
        let errorMessage = "Request failed";
        try {
            const errorData = await response.json();
            errorMessage = errorData.error || errorData.message || errorMessage;
        } catch {
            errorMessage = response.statusText || errorMessage;
        }
        throw new Error(errorMessage);
    }

    // Handle empty 200 OK responses (e.g., register endpoint)
    if (!expectBody) {
        return null;
    }

    // Parse JSON response
    return await response.json();
}

/**
 * Creates headers object with Authorization Bearer token
 * Automatically validates token freshness via getToken()
 */
export function createAuthHeaders(additional = {}, requireAuth = false) {
    const token = getToken(); // Auto-validates token

    const headers = {
        "Content-Type": "application/json",
        ...additional
    };

    // Inject Authorization only if token exists and is valid
    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    } else if (requireAuth) {
        throw new Error("Authentication required but no valid token found");
    }

    return headers;
}
