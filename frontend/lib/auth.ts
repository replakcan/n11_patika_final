import type { NextAuthOptions } from "next-auth";
import KeycloakProvider from "next-auth/providers/keycloak";

type KeycloakToken = {
  access_token?: string;
  realm_access?: { roles?: string[] };
  resource_access?: Record<string, { roles?: string[] }>;
  azp?: string;
  client_id?: string;
};

function decodeJwt(token?: string): KeycloakToken {
  if (!token) return {};
  const [, payload] = token.split(".");
  if (!payload) return {};
  try {
    return JSON.parse(Buffer.from(payload, "base64url").toString("utf8"));
  } catch {
    return {};
  }
}

function extractRoles(accessToken?: string) {
  const decoded = decodeJwt(accessToken);
  const roles = new Set<string>(decoded.realm_access?.roles ?? []);
  const clientId = decoded.azp || decoded.client_id;
  if (clientId) {
    decoded.resource_access?.[clientId]?.roles?.forEach((role) => roles.add(role));
  }
  return [...roles];
}

export const authOptions: NextAuthOptions = {
  providers: [
    KeycloakProvider({
      clientId: process.env.KEYCLOAK_CLIENT_ID ?? "frontend",
      clientSecret: process.env.KEYCLOAK_CLIENT_SECRET ?? "",
      issuer: process.env.KEYCLOAK_ISSUER ?? "http://localhost:8085/realms/ecommerce"
    })
  ],
  pages: {
    signIn: "/auth/signin",
    error: "/auth/error"
  },
  callbacks: {
    async jwt({ token, account }) {
      if (account?.access_token) {
        token.accessToken = account.access_token;
        token.roles = extractRoles(account.access_token);
      }
      return token;
    },
    async session({ session, token }) {
      session.accessToken = token.accessToken as string | undefined;
      session.roles = (token.roles as string[] | undefined) ?? [];
      return session;
    }
  }
};

export function hasRole(roles: string[] | undefined, role: "USER" | "ADMIN") {
  return Boolean(roles?.includes(role));
}

export function canShop(roles: string[] | undefined) {
  return hasRole(roles, "USER") || hasRole(roles, "ADMIN");
}
