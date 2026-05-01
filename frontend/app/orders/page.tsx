import Link from "next/link";
import { getServerSession } from "next-auth";

import { getOrders } from "@/lib/api";
import { authOptions } from "@/lib/auth";
import { formatDate, formatMoney } from "@/lib/utils";
import { OrderConfirmationToast } from "@/components/order-confirmation-toast";
import { OrderStatusBadge } from "@/components/order-status-badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

export const dynamic = "force-dynamic";

export default async function OrdersPage({
  searchParams
}: {
  searchParams?: { payment?: string; orderId?: string };
}) {
  const session = await getServerSession(authOptions);
  const orders = await getOrders(session!.accessToken!);
  const shouldShowConfirmationToast = searchParams?.payment === "confirmed";

  return (
    <div className="space-y-5">
      {shouldShowConfirmationToast ? <OrderConfirmationToast orderId={searchParams?.orderId} /> : null}
      <h1 className="text-3xl font-semibold tracking-tight">Orders</h1>
      {orders.length ? (
        orders.map((order) => (
          <Card key={order.id}>
            <CardContent className="flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <div className="flex items-center gap-3">
                  <h2 className="font-semibold">Order #{order.id}</h2>
                  <OrderStatusBadge status={order.status} />
                </div>
                <p className="mt-1 text-sm text-muted-foreground">{formatDate(order.createdAt)}</p>
              </div>
              <div className="flex items-center gap-4">
                <p className="font-semibold">{formatMoney(order.totalAmount)}</p>
                <Button asChild variant="outline">
                  <Link href={`/orders/${order.id}`}>View</Link>
                </Button>
              </div>
            </CardContent>
          </Card>
        ))
      ) : (
        <Card>
          <CardContent className="p-8 text-center text-muted-foreground">No orders yet.</CardContent>
        </Card>
      )}
    </div>
  );
}
