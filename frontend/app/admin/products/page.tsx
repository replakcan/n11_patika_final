import Link from "next/link";

import { getProducts } from "@/lib/api";
import { formatMoney } from "@/lib/utils";
import { DeleteProductButton } from "@/components/delete-product-button";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";

export const dynamic = "force-dynamic";

export default async function AdminProductsPage() {
  const products = await getProducts({ page: 0, size: 50, sort: "createdAt,desc" }).catch(() => null);

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-semibold tracking-tight">Product Admin</h1>
          <p className="mt-2 text-muted-foreground">Create, update, and remove catalog products.</p>
        </div>
        <Button asChild>
          <Link href="/admin/products/new">New product</Link>
        </Button>
      </div>
      {!products ? (
        <div className="rounded-lg border bg-card p-8 text-center text-muted-foreground">
          Product service is unavailable. Start the backend gateway on port 8079 and refresh.
        </div>
      ) : (
      <div className="overflow-hidden rounded-lg border bg-card">
        <table className="w-full text-left text-sm">
          <thead className="bg-muted text-muted-foreground">
            <tr>
              <th className="px-4 py-3 font-medium">Name</th>
              <th className="px-4 py-3 font-medium">Price</th>
              <th className="px-4 py-3 font-medium">Stock</th>
              <th className="px-4 py-3 font-medium">State</th>
              <th className="px-4 py-3 text-right font-medium">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {products.content.map((product) => (
              <tr key={product.id}>
                <td className="px-4 py-3 font-medium">{product.name}</td>
                <td className="px-4 py-3">{formatMoney(product.price)}</td>
                <td className="px-4 py-3">{product.stock}</td>
                <td className="px-4 py-3">
                  <Badge variant={product.active ? "secondary" : "outline"}>{product.active ? "Active" : "Inactive"}</Badge>
                </td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-2">
                    <Button asChild variant="outline" size="sm">
                      <Link href={`/admin/products/${product.id}/edit`}>Edit</Link>
                    </Button>
                    <DeleteProductButton productId={product.id} />
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      )}
    </div>
  );
}
