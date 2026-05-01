import { notFound } from "next/navigation";

import { getProduct } from "@/lib/api";
import { AdminProductForm } from "@/components/admin-product-form";

export const dynamic = "force-dynamic";

export default async function EditProductPage({ params }: { params: { id: string } }) {
  const product = await getProduct(Number(params.id)).catch(() => null);
  if (!product) notFound();

  return (
    <div className="space-y-5">
      <h1 className="text-3xl font-semibold tracking-tight">Edit Product</h1>
      <AdminProductForm product={product} />
    </div>
  );
}
