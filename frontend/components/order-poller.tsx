"use client";

import { useEffect, useRef, useState } from "react";
import { ExternalLink, Loader2 } from "lucide-react";

import type { Order } from "@/lib/types";
import { formatMoney } from "@/lib/utils";
import { OrderStatusBadge } from "@/components/order-status-badge";
import { Button } from "@/components/ui/button";

const ACTIVE_STATUSES = new Set(["PENDING", "STOCK_RESERVED", "PAYMENT_PENDING"]);

export function OrderPoller({ initialOrder }: { initialOrder: Order }) {
  const [order, setOrder] = useState(initialOrder);
  const redirectedRef = useRef(false);
  const paymentPageUrl = ACTIVE_STATUSES.has(order.status) ? order.paymentPageUrl : null;

  useEffect(() => {
    if (!ACTIVE_STATUSES.has(order.status) || order.paymentPageUrl) return;
    const id = window.setInterval(async () => {
      const response = await fetch(`/api/orders/${order.id}`);
      if (response.ok) setOrder(await response.json());
    }, 3000);
    return () => window.clearInterval(id);
  }, [order.id, order.paymentPageUrl, order.status]);

  useEffect(() => {
    if (!paymentPageUrl || redirectedRef.current) return;
    const redirectKey = `payment-redirected:${order.id}`;
    if (window.sessionStorage.getItem(redirectKey) === paymentPageUrl) return;
    window.sessionStorage.setItem(redirectKey, paymentPageUrl);
    redirectedRef.current = true;
    const id = window.setTimeout(() => {
      window.location.href = paymentPageUrl;
    }, 700);
    return () => window.clearTimeout(id);
  }, [order.id, paymentPageUrl]);

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold">Order #{order.id}</h1>
          <p className="text-sm text-muted-foreground">Total {formatMoney(order.totalAmount)}</p>
        </div>
        <OrderStatusBadge status={order.status} />
      </div>
      {paymentPageUrl ? (
        <div className="rounded-lg border bg-card p-4">
          <p className="mb-3 text-sm text-muted-foreground">
            Payment is ready. Redirecting to the secure checkout page.
          </p>
          <Button asChild>
            <a href={paymentPageUrl}>
              <ExternalLink className="h-4 w-4" />
              Open payment page
            </a>
          </Button>
        </div>
      ) : ACTIVE_STATUSES.has(order.status) ? (
        <div className="flex items-center gap-3 rounded-lg border bg-muted p-4 text-sm text-muted-foreground">
          <Loader2 className="h-4 w-4 animate-spin text-primary" />
          <p>Preparing payment. This page refreshes the order state automatically.</p>
        </div>
      ) : null}
    </div>
  );
}
