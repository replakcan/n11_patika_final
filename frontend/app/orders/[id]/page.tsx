import { notFound } from "next/navigation";
import { getServerSession } from "next-auth";

import { getOrder } from "@/lib/api";
import { authOptions } from "@/lib/auth";
import { formatMoney } from "@/lib/utils";
import { OrderPoller } from "@/components/order-poller";
import { Card, CardContent } from "@/components/ui/card";

export const dynamic = "force-dynamic";

export default async function OrderDetailPage({ params }: { params: { id: string } }) {
  const session = await getServerSession(authOptions);
  const order = await getOrder(Number(params.id), session!.accessToken!).catch(() => null);
  if (!order) notFound();

  return (
    <div className="space-y-6">
      <OrderPoller initialOrder={order} />
      <Card>
        <CardContent className="divide-y p-0">
          {order.items.map((item) => (
            <div key={item.id} className="flex items-center justify-between gap-4 p-5">
              <div>
                <h2 className="font-semibold">{item.productName}</h2>
                <p className="text-sm text-muted-foreground">
                  {formatMoney(item.unitPrice)} x {item.quantity}
                </p>
              </div>
              <p className="font-semibold">{formatMoney(item.lineTotal)}</p>
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}
