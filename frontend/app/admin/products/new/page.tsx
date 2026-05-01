import { AdminProductForm } from "@/components/admin-product-form";

export default function NewProductPage() {
  return (
    <div className="space-y-5">
      <h1 className="text-3xl font-semibold tracking-tight">New Product</h1>
      <AdminProductForm />
    </div>
  );
}
