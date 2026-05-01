import Link from "next/link";

import { getProducts } from "@/lib/api";
import { ProductCard } from "@/components/product-card";
import { Button } from "@/components/ui/button";

export const dynamic = "force-dynamic";

export default async function Home({
  searchParams
}: {
  searchParams: { page?: string; sort?: string };
}) {
  const page = Math.max(Number(searchParams.page ?? 0), 0);
  const products = await getProducts({ page, size: 12, sort: searchParams.sort }).catch(() => null);

  return (
    <div className="space-y-8">
      <section className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-semibold tracking-tight">Products</h1>
          <p className="mt-2 text-muted-foreground">Browse the current catalog from the product service.</p>
        </div>
        <div className="flex gap-2">
          <Button asChild variant="outline" size="sm">
            <Link href="/?sort=price,asc">Lowest price</Link>
          </Button>
          <Button asChild variant="outline" size="sm">
            <Link href="/?sort=createdAt,desc">Newest</Link>
          </Button>
        </div>
      </section>

      {!products ? (
        <div className="rounded-lg border bg-card p-8 text-center text-muted-foreground">
          Product service is unavailable. Start the backend gateway on port 8079 and refresh.
        </div>
      ) : products.content.length ? (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {products.content.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      ) : (
        <div className="rounded-lg border bg-card p-8 text-center text-muted-foreground">No products yet.</div>
      )}

      <div className="flex items-center justify-between">
        <Button asChild variant="outline" disabled={page <= 0}>
          <Link aria-disabled={page <= 0} href={`/?page=${Math.max(page - 1, 0)}`}>
            Previous
          </Link>
        </Button>
        <p className="text-sm text-muted-foreground">
          Page {(products?.number ?? 0) + 1} of {Math.max(products?.totalPages ?? 1, 1)}
        </p>
        <Button asChild variant="outline" disabled={!products || products.number + 1 >= products.totalPages}>
          <Link aria-disabled={!products || products.number + 1 >= products.totalPages} href={`/?page=${page + 1}`}>
            Next
          </Link>
        </Button>
      </div>
    </div>
  );
}
