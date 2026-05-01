"use client";

import { signIn } from "next-auth/react";
import { LogIn } from "lucide-react";

import { Button } from "@/components/ui/button";

export function SignInPanel() {
  return (
    <div className="mx-auto max-w-sm rounded-lg border bg-card p-6 text-center">
      <h1 className="text-2xl font-semibold">Sign in</h1>
      <p className="mt-2 text-sm text-muted-foreground">Use your Keycloak account to manage cart, orders, and admin tools.</p>
      <Button className="mt-6 w-full" onClick={() => signIn("keycloak", { callbackUrl: "/" }, { prompt: "login" })}>
        <LogIn className="h-4 w-4" />
        Continue with Keycloak
      </Button>
    </div>
  );
}
