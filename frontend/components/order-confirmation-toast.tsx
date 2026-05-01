"use client";

import { useEffect } from "react";
import { toast } from "sonner";

export function OrderConfirmationToast({ orderId }: { orderId?: string }) {
  useEffect(() => {
    toast.success(orderId ? `Order #${orderId} placed successfully.` : "Order placed successfully.");

    const url = new URL(window.location.href);
    url.searchParams.delete("payment");
    url.searchParams.delete("orderId");
    window.history.replaceState(null, "", `${url.pathname}${url.search}${url.hash}`);
  }, [orderId]);

  return null;
}
