"use client";

import { useEffect } from "react";
import { toast } from "sonner";

export function OrderConfirmationToast({ orderId }: { orderId?: string }) {
  useEffect(() => {
    const confirmationKey = orderId
      ? `order-confirmed:${orderId}`
      : `order-confirmed:${window.location.pathname}${window.location.search}`;

    if (window.sessionStorage.getItem(confirmationKey) === "shown") {
      return;
    }

    window.sessionStorage.setItem(confirmationKey, "shown");
    toast.success(orderId ? `Order #${orderId} placed successfully.` : "Order placed successfully.");

    const url = new URL(window.location.href);
    url.searchParams.delete("payment");
    url.searchParams.delete("orderId");
    window.history.replaceState(null, "", `${url.pathname}${url.search}${url.hash}`);
  }, [orderId]);

  return null;
}
