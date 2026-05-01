"use client";

import { useFormState, useFormStatus } from "react-dom";

import { saveProductAction, type ActionState } from "@/app/actions";
import type { Product } from "@/lib/types";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";

const initialState: ActionState = { ok: false };

function SubmitButton() {
  const { pending } = useFormStatus();
  return <Button disabled={pending}>{pending ? "Saving..." : "Save product"}</Button>;
}

export function AdminProductForm({ product }: { product?: Product }) {
  const action = saveProductAction.bind(null, product?.id);
  const [state, formAction] = useFormState(action, initialState);

  return (
    <form action={formAction} className="max-w-2xl space-y-5">
      <div className="grid gap-2">
        <Label htmlFor="name">Name</Label>
        <Input id="name" name="name" required defaultValue={product?.name ?? ""} />
      </div>
      <div className="grid gap-2">
        <Label htmlFor="description">Description</Label>
        <Textarea id="description" name="description" required maxLength={1000} defaultValue={product?.description ?? ""} />
      </div>
      <div className="grid gap-4 sm:grid-cols-2">
        <div className="grid gap-2">
          <Label htmlFor="price">Price</Label>
          <Input id="price" name="price" type="number" min="0.01" step="0.01" required defaultValue={String(product?.price ?? "")} />
        </div>
        <div className="grid gap-2">
          <Label htmlFor="stock">Stock</Label>
          <Input id="stock" name="stock" type="number" min="0" required defaultValue={String(product?.stock ?? "")} />
        </div>
      </div>
      <div className="grid gap-2">
        <Label htmlFor="imageUrl">Image URL</Label>
        <Input id="imageUrl" name="imageUrl" type="url" defaultValue={product?.imageUrl ?? ""} />
      </div>
      <label className="flex items-center gap-2 text-sm font-medium">
        <input name="active" type="checkbox" defaultChecked={product?.active ?? true} className="h-4 w-4 rounded border-input" />
        Active
      </label>
      {state.message ? <p className="rounded-md border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">{state.message}</p> : null}
      <SubmitButton />
    </form>
  );
}
