import { NextResponse } from "next/server";

export function GET() {
  const issuer = process.env.KEYCLOAK_ISSUER ?? "http://localhost:8085/realms/ecommerce";
  const clientId = process.env.KEYCLOAK_CLIENT_ID ?? "frontend";
  const redirectUri = process.env.NEXTAUTH_URL ?? "http://localhost:3000";

  const logoutUrl = new URL(`${issuer.replace(/\/$/, "")}/protocol/openid-connect/logout`);
  logoutUrl.searchParams.set("client_id", clientId);
  logoutUrl.searchParams.set("post_logout_redirect_uri", redirectUri);

  return NextResponse.redirect(logoutUrl);
}
