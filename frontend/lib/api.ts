import "server-only";

import { ApiError, normalizeApiError } from "@/lib/api-error";
import type { Cart, CartItem, ErrorResponse, Order, Product, ProductPage } from "@/lib/types";

export { ApiError, normalizeApiError };

type NextRequestInit = RequestInit & { next?: { revalidate?: number } };
type ProductCreateInput = {
  name: string;
  description: string;
  price: number | string;
  stock: number;
  imageUrl?: string | null;
};
type ProductUpdateInput = ProductCreateInput & { active: boolean };

export function getApiBaseUrl() {
  return process.env.API_BASE_URL || process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8079";
}

async function parseResponse(response: Response) {
  const text = await response.text();
  if (!text) return undefined;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

async function request<T>(path: string, init: NextRequestInit = {}, accessToken?: string): Promise<T> {
  const headers = new Headers(init.headers);
  if (!headers.has("Content-Type") && init.body) headers.set("Content-Type", "application/json");
  if (accessToken) headers.set("Authorization", `Bearer ${accessToken}`);

  const response = await fetch(`${getApiBaseUrl()}${path}`, {
    ...init,
    headers,
    cache: init.cache ?? "no-store"
  });

  const payload = await parseResponse(response);
  if (!response.ok) {
    const message =
      typeof payload === "object" && payload && "message" in payload
        ? String((payload as ErrorResponse).message)
        : `Request failed with status ${response.status}`;
    throw new ApiError(message, response.status, payload);
  }

  return payload as T;
}

export function getProducts(params: { page?: number; size?: number; sort?: string } = {}) {
  const search = new URLSearchParams();
  search.set("page", String(params.page ?? 0));
  search.set("size", String(params.size ?? 12));
  if (params.sort) search.set("sort", params.sort);
  return request<ProductPage>(`/api/products?${search.toString()}`, { next: { revalidate: 30 } });
}

export function getProduct(id: number) {
  return request<Product>(`/api/products/${id}`, { next: { revalidate: 30 } });
}

export function createProduct(input: ProductCreateInput, token: string) {
  return request<Product>("/api/products", { method: "POST", body: JSON.stringify(input) }, token);
}

export function updateProduct(id: number, input: ProductUpdateInput, token: string) {
  return request<Product>(`/api/products/${id}`, { method: "PUT", body: JSON.stringify(input) }, token);
}

export function deleteProduct(id: number, token: string) {
  return request<void>(`/api/products/${id}`, { method: "DELETE" }, token);
}

export function getCart(token: string) {
  return request<Cart>("/api/cart", {}, token);
}

export function addCartItem(productId: number, quantity: number, token: string) {
  return request<CartItem>("/api/cart/items", { method: "POST", body: JSON.stringify({ productId, quantity }) }, token);
}

export function updateCartItem(itemId: number, quantity: number, token: string) {
  return request<CartItem>(`/api/cart/items/${itemId}`, { method: "PUT", body: JSON.stringify({ quantity }) }, token);
}

export function deleteCartItem(itemId: number, token: string) {
  return request<void>(`/api/cart/items/${itemId}`, { method: "DELETE" }, token);
}

export function getOrders(token: string) {
  return request<Order[]>("/api/orders", {}, token);
}

export function getOrder(id: number, token: string) {
  return request<Order>(`/api/orders/${id}`, {}, token);
}

export function placeOrder(token: string) {
  return request<Order>("/api/orders", { method: "POST" }, token);
}
