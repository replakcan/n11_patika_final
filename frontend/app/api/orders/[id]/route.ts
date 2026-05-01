import { NextResponse } from "next/server";
import { getServerSession } from "next-auth";

import { getOrder, normalizeApiError } from "@/lib/api";
import { authOptions } from "@/lib/auth";

export async function GET(_request: Request, { params }: { params: { id: string } }) {
  const session = await getServerSession(authOptions);
  if (!session?.accessToken) {
    return NextResponse.json({ message: "Unauthorized" }, { status: 401 });
  }

  try {
    const order = await getOrder(Number(params.id), session.accessToken);
    return NextResponse.json(order);
  } catch (error) {
    return NextResponse.json({ message: normalizeApiError(error) }, { status: 500 });
  }
}
