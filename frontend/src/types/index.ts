export interface Product {
  id: string;
  external_id: string;
  name: string;
  brand: string | null;
  category: 'telefona' | 'wearables';
  price_original: number | null;
  image_url: string | null;
  product_url: string | null;
  description: string | null;
  specs: Record<string, string> | null;
  is_available: boolean;
  scraped_at: string | null;
  created_at: string;
  discount_percentage: number | null;
  final_price: number | null;
  reasoning: string | null;
  discount_expires_at: string | null;
}

export interface ProductListResponse {
  total: number;
  page: number;
  limit: number;
  items: Product[];
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export type PaymentMethod = 'prepaid' | 'postpaid';

export interface CardDetails {
  cardNumber: string;
  cardHolder: string;
  expiry: string;
  cvv: string;
}

export interface InvoiceData {
  invoiceNumber: string;
  date: string;
  product: Product;
  quantity: number;
  subtotal: number;
  discount: number;
  total: number;
  paymentMethod: PaymentMethod;
  cardLast4?: string;
}
