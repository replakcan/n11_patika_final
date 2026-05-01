import Image from "next/image";
import { notFound } from "next/navigation";

import { getProduct } from "@/lib/api";
import { formatDate, formatMoney } from "@/lib/utils";
import { AddToCartButton } from "@/components/add-to-cart-button";
import { Badge } from "@/components/ui/badge";

export const dynamic = "force-dynamic";

export default async function ProductDetail({ params }: { params: { id: string } }) {
  const id = Number(params.id);
  const product = await getProduct(id).catch(() => null);
  if (!product) notFound();

  const unavailable = !product.active || product.stock <= 0;

  return (
    <div className="grid gap-8 lg:grid-cols-[minmax(0,1fr)_420px]">
      <div className="relative aspect-[4/3] overflow-hidden rounded-lg border bg-muted">
        {product.imageUrl ? (
          <Image src={product.imageUrl} alt={product.name} fill className="object-cover" priority />
        ) : (
          <div className="flex h-full items-center justify-center text-muted-foreground">No image</div>
        )}
      </div>
      <section className="space-y-6">
        <div className="space-y-3">
          <div className="flex items-center gap-2">
            {unavailable ? <Badge variant="outline">Unavailable</Badge> : <Badge>{product.stock} in stock</Badge>}
          </div>
          <h1 className="text-3xl font-semibold tracking-tight">{product.name}</h1>
          <p className="text-2xl font-semibold">{formatMoney(product.price)}</p>
          <p className="leading-7 text-muted-foreground">{product.description}</p>
        </div>
        <AddToCartButton productId={product.id} maxQuantity={product.stock} disabled={unavailable} />
        <dl className="grid grid-cols-2 gap-4 rounded-lg border p-4 text-sm">
          <div>
            <dt className="text-muted-foreground">Created</dt>
            <dd>{formatDate(product.createdAt)}</dd>
          </div>
          <div>
            <dt className="text-muted-foreground">Updated</dt>
            <dd>{formatDate(product.updatedAt)}</dd>
          </div>
        </dl>
      </section>
    </div>
  );
}
