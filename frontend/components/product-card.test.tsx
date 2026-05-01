import React from "react";
import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";

vi.mock("next-auth/react", () => ({
  useSession: () => ({ status: "unauthenticated" })
}));

vi.mock("@/components/add-to-cart-button", () => ({
  AddToCartButton: () => <button>Sign in</button>
}));

import { ProductCard } from "@/components/product-card";

describe("ProductCard", () => {
  it("renders product summary and sign-in call to action", () => {
    render(
      <ProductCard
        product={{
          id: 1,
          name: "Test Product",
          description: "A product for tests",
          price: 125,
          stock: 4,
          active: true,
          imageUrl: null
        }}
      />
    );

    expect(screen.getByText("Test Product")).toBeInTheDocument();
    expect(screen.getByText("4 left")).toBeInTheDocument();
    expect(screen.getByText("Sign in")).toBeInTheDocument();
  });
});
