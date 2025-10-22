// Environment-specific API configuration
export const baseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

// Future: add other config like timeouts, retry logic, etc.
export const config = {
    apiTimeout: 30000,
    retryAttempts: 3
};
