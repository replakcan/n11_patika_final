import type { OrderStatus } from "@/lib/types";
import { Badge } from "@/components/ui/badge";

export function OrderStatusBadge({ status }: { status: OrderStatus }) {
  const variant = status === "FAILED" || status === "CANCELLED" ? "destructive" : status === "CONFIRMED" ? "default" : "secondary";
  return <Badge variant={variant}>{status.replace("_", " ")}</Badge>;
}
