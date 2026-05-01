import React from "react";
import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

vi.mock("@/app/actions", () => ({
  saveProductAction: vi.fn()
}));

vi.mock("react-dom", async () => {
  const actual = await vi.importActual<typeof import("react-dom")>("react-dom");
  return {
    ...actual,
    useFormState: () => [{ ok: false }, vi.fn()],
    useFormStatus: () => ({ pending: false })
  };
});

import { AdminProductForm } from "@/components/admin-product-form";

describe("AdminProductForm", () => {
  it("renders backend-aligned required fields", () => {
    render(<AdminProductForm />);
    expect(screen.getByLabelText("Name")).toBeRequired();
    expect(screen.getByLabelText("Description")).toHaveAttribute("maxLength", "1000");
    expect(screen.getByLabelText("Price")).toHaveAttribute("min", "0.01");
    expect(screen.getByLabelText("Stock")).toHaveAttribute("min", "0");
  });
});
