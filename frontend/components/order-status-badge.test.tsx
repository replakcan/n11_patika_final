import React from "react";
import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import { OrderStatusBadge } from "@/components/order-status-badge";

describe("OrderStatusBadge", () => {
  it("renders readable status text", () => {
    render(<OrderStatusBadge status="PAYMENT_PENDING" />);
    expect(screen.getByText("PAYMENT PENDING")).toBeInTheDocument();
  });
});
