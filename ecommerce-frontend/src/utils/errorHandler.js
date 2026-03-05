export const getErrorMessage = (error) => {
  if (error.response) {
    const { data, status } = error.response;

    if (data?.message) {
      return data.message;
    }

    if (data?.fieldErrors && Array.isArray(data.fieldErrors)) {
      return data.fieldErrors.map((e) => e.message).join(', ');
    }

    switch (status) {
      case 400:
        return 'Invalid request. Please check your input.';
      case 401:
        return 'Please log in to continue.';
      case 403:
        return 'You do not have permission to perform this action.';
      case 404:
        return 'The requested resource was not found.';
      case 409:
        return 'This resource already exists.';
      case 500:
        return 'An unexpected error occurred. Please try again later.';
      default:
        return 'An error occurred. Please try again.';
    }
  }

  if (error.request) {
    return 'Unable to connect to the server. Please check your internet connection.';
  }

  return error.message || 'An unexpected error occurred.';
};

export const handleApiError = (error, setError) => {
  const message = getErrorMessage(error);
  if (setError) {
    setError(message);
  }
  console.error('API Error:', error);
  return message;
};

export default { getErrorMessage, handleApiError };
