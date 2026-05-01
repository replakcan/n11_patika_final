import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

import { addToCartAction } from "@/app/actions";
import { AddToCartButton } from "@/components/add-to-cart-button";
import { toast } from "sonner";

vi.mock("next-auth/react", () => ({
  useSession: () => ({ status: "authenticated" })
}));

vi.mock("@/app/actions", () => ({
  addToCartAction: vi.fn()
}));

vi.mock("sonner", () => ({
  toast: {
    error: vi.fn(),
    success: vi.fn()
  }
}));

describe("AddToCartButton", () => {
  it("shows a success toast without rendering inline status text", async () => {
    vi.mocked(addToCartAction).mockResolvedValue({ ok: true, message: "Added to cart." });

    render(<AddToCartButton productId={1} maxQuantity={4} />);

    fireEvent.click(screen.getByRole("button", { name: /add/i }));

    await waitFor(() => {
      expect(toast.success).toHaveBeenCalledWith("Added to cart.");
    });

    expect(screen.queryByText("Added to cart.")).not.toBeInTheDocument();
  });

  it("shows an error toast when adding to cart fails", async () => {
    vi.mocked(addToCartAction).mockResolvedValue({ ok: false, message: "Not enough stock." });

    render(<AddToCartButton productId={1} maxQuantity={4} />);

    fireEvent.click(screen.getByRole("button", { name: /add/i }));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Not enough stock.");
    });
  });
});
