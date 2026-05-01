export type Product = {
  id: number;
  name: string;
  description: string;
  price: number | string;
  stock: number;
  imageUrl?: string | null;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
};

export type ProductPage = {
  content: Product[];
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
  first?: boolean;
  last?: boolean;
};

export type ProductFormValues = {
  name: string;
  description: string;
  price: string;
  stock: string;
  imageUrl: string;
  active: boolean;
};

export type CartItem = {
  id: number;
  productId: number;
  quantity: number;
  createdAt?: string;
  updatedAt?: string;
};

export type Cart = {
  userId: string;
  totalItems: number;
  items: CartItem[];
};

export type OrderStatus =
  | "PENDING"
  | "STOCK_RESERVED"
  | "PAYMENT_PENDING"
  | "CONFIRMED"
  | "FAILED"
  | "CANCELLED";

export type OrderItem = {
  id: number;
  productId: number;
  productName: string;
  unitPrice: number | string;
  quantity: number;
  lineTotal: number | string;
};

export type Order = {
  id: number;
  userId: string;
  status: OrderStatus;
  totalAmount: number | string;
  paymentPageUrl?: string | null;
  items: OrderItem[];
  createdAt?: string;
  updatedAt?: string;
};

export type ErrorResponse = {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
};
