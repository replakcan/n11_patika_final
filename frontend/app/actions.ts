"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { getServerSession } from "next-auth";

import {
  addCartItem,
  createProduct,
  deleteCartItem,
  deleteProduct,
  normalizeApiError,
  placeOrder,
  updateCartItem,
  updateProduct
} from "@/lib/api";
import { authOptions, hasRole } from "@/lib/auth";
import type { Product } from "@/lib/types";

export type ActionState = {
  ok: boolean;
  message?: string;
};

function revalidateCartViews() {
  revalidatePath("/", "layout");
  revalidatePath("/cart");
}

async function requireToken() {
  const session = await getServerSession(authOptions);
  if (!session?.accessToken) redirect("/auth/signin");
  return session.accessToken;
}

async function requireAdminToken() {
  const session = await getServerSession(authOptions);
  if (!session?.accessToken) redirect("/auth/signin");
  if (!hasRole(session.roles, "ADMIN")) redirect("/");
  return session.accessToken;
}

export async function addToCartAction(productId: number, quantity: number): Promise<ActionState> {
  try {
    const token = await requireToken();
    await addCartItem(productId, quantity, token);
    revalidateCartViews();
    return { ok: true, message: "Added to cart." };
  } catch (error) {
    return { ok: false, message: normalizeApiError(error) };
  }
}

export async function updateCartItemAction(itemId: number, quantity: number): Promise<ActionState> {
  try {
    const token = await requireToken();
    await updateCartItem(itemId, quantity, token);
    revalidateCartViews();
    return { ok: true };
  } catch (error) {
    return { ok: false, message: normalizeApiError(error) };
  }
}

export async function deleteCartItemAction(itemId: number): Promise<ActionState> {
  try {
    const token = await requireToken();
    await deleteCartItem(itemId, token);
    revalidateCartViews();
    return { ok: true };
  } catch (error) {
    return { ok: false, message: normalizeApiError(error) };
  }
}

export async function placeOrderAction(): Promise<ActionState> {
  let orderId: number | undefined;
  try {
    const token = await requireToken();
    const order = await placeOrder(token);
    orderId = order.id;
  } catch (error) {
    return { ok: false, message: normalizeApiError(error) };
  }
  revalidatePath("/orders");
  redirect(`/orders/${orderId}`);
}

function productPayload(formData: FormData): Omit<Product, "id" | "createdAt" | "updatedAt"> {
  return {
    name: String(formData.get("name") ?? "").trim(),
    description: String(formData.get("description") ?? "").trim(),
    price: Number(formData.get("price")),
    stock: Number(formData.get("stock")),
    imageUrl: String(formData.get("imageUrl") ?? "").trim() || null,
    active: formData.get("active") === "on"
  };
}

export async function saveProductAction(productId: number | undefined, _state: ActionState, formData: FormData) {
  try {
    const token = await requireAdminToken();
    const payload = productPayload(formData);
    if (productId) {
      await updateProduct(productId, payload, token);
    } else {
      const { active: _active, ...createPayload } = payload;
      await createProduct(createPayload, token);
    }
  } catch (error) {
    return { ok: false, message: normalizeApiError(error) };
  }

  revalidatePath("/");
  revalidatePath("/admin/products");
  redirect("/admin/products");
}

export async function deleteProductAction(productId: number): Promise<ActionState> {
  try {
    const token = await requireAdminToken();
    await deleteProduct(productId, token);
    revalidatePath("/");
    revalidatePath("/admin/products");
    return { ok: true };
  } catch (error) {
    return { ok: false, message: normalizeApiError(error) };
  }
}
