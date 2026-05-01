import { withAuth } from "next-auth/middleware";
import { NextResponse } from "next/server";

export default withAuth(
  function middleware(req) {
    const roles = (req.nextauth.token?.roles as string[] | undefined) ?? [];
    if (req.nextUrl.pathname.startsWith("/admin") && !roles.includes("ADMIN")) {
      return NextResponse.redirect(new URL("/", req.url));
    }
    return NextResponse.next();
  },
  {
    callbacks: {
      authorized: ({ token, req }) => {
        const pathname = req.nextUrl.pathname;
        if (pathname.startsWith("/cart") || pathname.startsWith("/orders") || pathname.startsWith("/admin")) {
          return Boolean(token);
        }
        return true;
      }
    }
  }
);

export const config = {
  matcher: ["/cart/:path*", "/orders/:path*", "/admin/:path*"]
};
