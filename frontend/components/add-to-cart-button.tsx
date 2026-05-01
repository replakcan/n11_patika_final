"use client";

import { useState, useTransition } from "react";
import { ShoppingCart } from "lucide-react";
import { useSession } from "next-auth/react";
import Link from "next/link";
import { toast } from "sonner";

import { addToCartAction } from "@/app/actions";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export function AddToCartButton({
  productId,
  maxQuantity,
  disabled = false
}: {
  productId: number;
  maxQuantity: number;
  disabled?: boolean;
}) {
  const { status } = useSession();
  const [quantity, setQuantity] = useState(1);
  const [pending, startTransition] = useTransition();

  if (status === "unauthenticated") {
    return (
      <Button asChild className="flex-1">
        <Link href="/auth/signin">
          <ShoppingCart className="h-4 w-4" />
          Sign in
        </Link>
      </Button>
    );
  }

  return (
    <div className="flex flex-1 flex-col gap-2">
      <div className="flex gap-2">
        <Input
          aria-label="Quantity"
          className="w-20"
          min={1}
          max={Math.max(maxQuantity, 1)}
          type="number"
          value={quantity}
          onChange={(event) => setQuantity(Number(event.target.value))}
          disabled={disabled || pending}
        />
        <Button
          className="flex-1"
          disabled={disabled || pending || quantity < 1 || quantity > maxQuantity}
          onClick={() =>
            startTransition(async () => {
              const result = await addToCartAction(productId, quantity);
              if (result.ok) {
                toast.success(result.message ?? "Added to cart.");
              } else {
                toast.error(result.message ?? "Could not add product to cart.");
              }
            })
          }
        >
          <ShoppingCart className="h-4 w-4" />
          Add
        </Button>
      </div>
    </div>
  );
}
