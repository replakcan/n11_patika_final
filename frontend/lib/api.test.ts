import { describe, expect, it } from "vitest";

import { ApiError, normalizeApiError } from "@/lib/api-error";

describe("api helpers", () => {
  it("normalizes ApiError messages", () => {
    expect(normalizeApiError(new ApiError("Product not found", 404))).toBe("Product not found");
  });

  it("normalizes unknown errors", () => {
    expect(normalizeApiError("nope")).toBe("Unexpected error. Please try again.");
  });
});
