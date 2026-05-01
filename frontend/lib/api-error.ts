import type { ErrorResponse } from "@/lib/types";

export class ApiError extends Error {
  status: number;
  payload?: ErrorResponse | unknown;

  constructor(message: string, status: number, payload?: ErrorResponse | unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.payload = payload;
  }
}

export function normalizeApiError(error: unknown) {
  if (error instanceof ApiError) {
    return error.message;
  }
  if (error instanceof Error) {
    return error.message;
  }
  return "Unexpected error. Please try again.";
}
