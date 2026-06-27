import axios from 'axios';

const api = axios.create({
  baseURL: '', // Vite proxy will forward request matching /api to http://localhost:8080
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response interceptor to format success and map API error structures
api.interceptors.response.use(
  (response) => {
    // Backend API typically wraps responses in ApiResponse DTO: { success: true, message: "...", data: ... }
    // Or returns standard raw Spring Data/Swagger schemas directly (like search endpoint)
    return response.data;
  },
  (error) => {
    const errorResponse = {
      message: 'An unexpected error occurred.',
      status: error.response?.status || 500,
      validationErrors: null,
    };

    if (error.response) {
      const data = error.response.data;

      // Handle standard ApiResponse wrap error
      if (data && typeof data === 'object') {
        errorResponse.message = data.message || errorResponse.message;
        
        // If there are validation errors (field-level exceptions mapped by GlobalExceptionHandler)
        if (data.errors && typeof data.errors === 'object') {
          errorResponse.validationErrors = data.errors;
        }
      }
    } else if (error.request) {
      errorResponse.message = 'No response received from server. Please check if the backend is running.';
    } else {
      errorResponse.message = error.message;
    }

    return Promise.reject(errorResponse);
  }
);

export default api;
