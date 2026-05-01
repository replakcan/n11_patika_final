import Link from "next/link";

import { Button } from "@/components/ui/button";

export default function AuthErrorPage() {
  return (
    <div className="mx-auto max-w-sm rounded-lg border bg-card p-6 text-center">
      <h1 className="text-2xl font-semibold">Authentication failed</h1>
      <p className="mt-2 text-sm text-muted-foreground">Check the Keycloak client configuration and try again.</p>
      <Button asChild className="mt-6">
        <Link href="/auth/signin">Back to sign in</Link>
      </Button>
    </div>
  );
}
