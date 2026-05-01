"use client";

import { useState, useTransition } from "react";
import { CreditCard } from "lucide-react";

import { placeOrderAction } from "@/app/actions";
import { Button } from "@/components/ui/button";

export function CheckoutButton({ disabled }: { disabled?: boolean }) {
  const [message, setMessage] = useState<string>();
  const [pending, startTransition] = useTransition();

  return (
    <div className="flex flex-col gap-2">
      <Button
        disabled={disabled || pending}
        onClick={() =>
          startTransition(async () => {
            const result = await placeOrderAction();
            if (result?.message) setMessage(result.message);
          })
        }
      >
        <CreditCard className="h-4 w-4" />
        Place order
      </Button>
      {message ? <p className="text-sm text-destructive">{message}</p> : null}
    </div>
  );
}
