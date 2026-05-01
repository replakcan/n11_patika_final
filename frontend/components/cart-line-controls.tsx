"use client";

import { useState, useTransition } from "react";
import { Trash2 } from "lucide-react";

import { deleteCartItemAction, updateCartItemAction } from "@/app/actions";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export function CartLineControls({ itemId, quantity }: { itemId: number; quantity: number }) {
  const [value, setValue] = useState(quantity);
  const [message, setMessage] = useState<string>();
  const [pending, startTransition] = useTransition();

  return (
    <div className="flex flex-col items-end gap-2">
      <div className="flex gap-2">
        <Input
          aria-label="Quantity"
          className="w-20"
          min={1}
          type="number"
          value={value}
          onChange={(event) => setValue(Number(event.target.value))}
        />
        <Button
          variant="outline"
          disabled={pending || value < 1}
          onClick={() =>
            startTransition(async () => {
              const result = await updateCartItemAction(itemId, value);
              setMessage(result.message);
            })
          }
        >
          Update
        </Button>
        <Button
          variant="ghost"
          size="icon"
          aria-label="Remove item"
          disabled={pending}
          onClick={() =>
            startTransition(async () => {
              const result = await deleteCartItemAction(itemId);
              setMessage(result.message);
            })
          }
        >
          <Trash2 className="h-4 w-4" />
        </Button>
      </div>
      {message ? <p className="text-xs text-destructive">{message}</p> : null}
    </div>
  );
}
