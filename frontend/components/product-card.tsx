import Image from "next/image";
import Link from "next/link";

import type { Product } from "@/lib/types";
import { formatMoney } from "@/lib/utils";
import { AddToCartButton } from "@/components/add-to-cart-button";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";

export function ProductCard({ product }: { product: Product }) {
  const unavailable = !product.active || product.stock <= 0;

  return (
    <Card className="flex h-full flex-col overflow-hidden">
      <Link href={`/products/${product.id}`} className="relative block aspect-[4/3] bg-muted">
        {product.imageUrl ? (
          <Image src={product.imageUrl} alt={product.name} fill className="object-cover" sizes="(min-width: 1024px) 25vw, 50vw" />
        ) : (
          <div className="flex h-full items-center justify-center text-sm text-muted-foreground">No image</div>
        )}
      </Link>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between gap-3">
          <CardTitle className="line-clamp-2 text-base leading-5">{product.name}</CardTitle>
          {unavailable ? <Badge variant="outline">Unavailable</Badge> : <Badge>{product.stock} left</Badge>}
        </div>
      </CardHeader>
      <CardContent className="flex-1">
        <p className="line-clamp-2 text-sm text-muted-foreground">{product.description}</p>
        <p className="mt-4 text-lg font-semibold">{formatMoney(product.price)}</p>
      </CardContent>
      <CardFooter className="gap-2">
        <Button asChild variant="outline" className="flex-1">
          <Link href={`/products/${product.id}`}>Details</Link>
        </Button>
        <AddToCartButton productId={product.id} maxQuantity={product.stock} disabled={unavailable} />
      </CardFooter>
    </Card>
  );
}
