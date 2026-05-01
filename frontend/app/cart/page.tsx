import { getServerSession } from "next-auth";

import { getCart, getProduct } from "@/lib/api";
import { authOptions } from "@/lib/auth";
import { formatMoney } from "@/lib/utils";
import { CartLineControls } from "@/components/cart-line-controls";
import { CheckoutButton } from "@/components/checkout-button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export const dynamic = "force-dynamic";

export default async function CartPage() {
  const session = await getServerSession(authOptions);
  const cart = await getCart(session!.accessToken!);
  const products = await Promise.all(cart.items.map((item) => getProduct(item.productId).catch(() => null)));
  const rows = cart.items.map((item, index) => ({ item, product: products[index] }));
  const total = rows.reduce((sum, row) => sum + Number(row.product?.price ?? 0) * row.item.quantity, 0);

  return (
    <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_320px]">
      <section className="space-y-4">
        <h1 className="text-3xl font-semibold tracking-tight">Cart</h1>
        {rows.length ? (
          rows.map(({ item, product }) => (
            <Card key={item.id}>
              <CardContent className="flex flex-col gap-4 p-5 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h2 className="font-semibold">{product?.name ?? `Product #${item.productId}`}</h2>
                  <p className="text-sm text-muted-foreground">
                    {formatMoney(product?.price)} x {item.quantity}
                  </p>
                  <p className="mt-1 text-sm font-medium">{formatMoney(Number(product?.price ?? 0) * item.quantity)}</p>
                </div>
                <CartLineControls itemId={item.id} quantity={item.quantity} />
              </CardContent>
            </Card>
          ))
        ) : (
          <Card>
            <CardContent className="p-8 text-center text-muted-foreground">Your cart is empty.</CardContent>
          </Card>
        )}
      </section>
      <Card className="h-fit">
        <CardHeader>
          <CardTitle>Summary</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex justify-between text-sm">
            <span className="text-muted-foreground">Items</span>
            <span>{cart.totalItems}</span>
          </div>
          <div className="flex justify-between text-lg font-semibold">
            <span>Total</span>
            <span>{formatMoney(total)}</span>
          </div>
          <CheckoutButton disabled={!rows.length} />
        </CardContent>
      </Card>
    </div>
  );
}
