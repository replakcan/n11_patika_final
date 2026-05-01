import Link from "next/link";
import { getServerSession } from "next-auth";
import { ShoppingCart, Store, UserRound } from "lucide-react";

import { getCart } from "@/lib/api";
import { authOptions, hasRole } from "@/lib/auth";
import { Button } from "@/components/ui/button";
import { SignOutButton } from "@/components/sign-out-button";

export async function SiteHeader() {
  const session = await getServerSession(authOptions);
  const isAdmin = hasRole(session?.roles, "ADMIN");
  const cart = session?.accessToken ? await getCart(session.accessToken).catch(() => null) : null;
  const cartItemCount = cart?.totalItems ?? 0;

  return (
    <header className="sticky top-0 z-40 border-b bg-background/95 backdrop-blur">
      <div className="container flex h-16 items-center justify-between gap-4">
        <Link href="/" className="flex items-center gap-2 text-base font-semibold">
          <Store className="h-5 w-5 text-primary" />
          n11 Patika
        </Link>
        <nav className="flex items-center gap-1 sm:gap-2">
          {isAdmin ? (
            <Button asChild variant="ghost" size="sm">
              <Link href="/admin/products">Admin</Link>
            </Button>
          ) : null}
          <Button asChild variant="ghost" size="sm">
            <Link href="/orders">Orders</Link>
          </Button>
          <Button asChild variant="ghost" size="icon" aria-label="Cart">
            <Link href="/cart" className="relative">
              <ShoppingCart className="h-4 w-4" />
              {cartItemCount > 0 ? (
                <span className="absolute -right-1 -top-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-primary px-1 text-[10px] font-semibold leading-none text-primary-foreground">
                  {cartItemCount > 99 ? "99+" : cartItemCount}
                </span>
              ) : null}
            </Link>
          </Button>
          {session ? (
            <SignOutButton />
          ) : (
            <Button asChild size="sm">
              <Link href="/auth/signin">
                <UserRound className="h-4 w-4" />
                Sign in
              </Link>
            </Button>
          )}
        </nav>
      </div>
    </header>
  );
}
