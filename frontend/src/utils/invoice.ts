import type { InvoiceData } from '../types';

export function generateInvoicePDF(data: InvoiceData): void {
    const printContent = buildInvoiceHTML(data);
    const win = window.open('', '_blank', 'width=800,height=900');
    if (!win) {
        alert('Please allow popups to download the invoice.');
        return;
    }
    win.document.write(printContent);
    win.document.close();
    win.focus();
    setTimeout(() => {
        win.print();
    }, 500);
}

function formatPrice(n: number): string {
    return n.toLocaleString('sq-AL') + ' L';
}

function buildInvoiceHTML(data: InvoiceData): string {
    const {
        invoiceNumber, date, product, quantity,
        subtotal, discount, total, paymentMethod, cardLast4
    } = data;

    const payLabel = paymentMethod === 'prepaid'
        ? `Credit/Debit Card ending in ${cardLast4 ?? '****'}`
        : 'Added to monthly postpaid bill';

    const specs = product.specs
        ? Object.entries(product.specs)
            .map(([k, v]) => `<tr><td style="color:#6B7280;padding:4px 0">${k}</td><td style="font-weight:500;padding:4px 0">${v}</td></tr>`)
            .join('')
        : '';

    return `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<title>Invoice ${invoiceNumber}</title>
<style>
  @import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500;600;700&display=swap');
  *{box-sizing:border-box;margin:0;padding:0}
  body{font-family:'DM Sans',sans-serif;background:#fff;color:#111;padding:40px;max-width:680px;margin:0 auto}
  .header{display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:40px;padding-bottom:28px;border-bottom:3px solid #5B1E8C}
  .logo{display:flex;align-items:center;gap:8px}
  .logo-box{width:36px;height:36px;background:#5B1E8C;border-radius:8px;display:flex;align-items:center;justify-content:center;color:#F5A623;font-weight:700;font-size:18px}
  .logo-text{font-size:22px;font-weight:700;color:#5B1E8C}
  .invoice-meta{text-align:right}
  .invoice-meta h1{font-size:28px;font-weight:700;color:#5B1E8C}
  .invoice-meta p{color:#6B7280;font-size:14px;margin-top:4px}
  .section{margin-bottom:28px}
  .section h3{font-size:11px;font-weight:600;color:#9CA3AF;text-transform:uppercase;letter-spacing:.08em;margin-bottom:12px}
  .product-card{background:#F8F6FB;border-radius:12px;padding:20px;display:flex;gap:20px;align-items:flex-start}
  .product-icon{width:64px;height:64px;background:#5B1E8C;border-radius:10px;display:flex;align-items:center;justify-content:center;flex-shrink:0;font-size:28px}
  .product-name{font-size:18px;font-weight:600;margin-bottom:4px}
  .product-brand{color:#6B7280;font-size:14px}
  table.specs{width:100%;border-collapse:collapse;font-size:14px;margin-top:12px}
  .totals{background:#F8F6FB;border-radius:12px;padding:20px}
  .totals-row{display:flex;justify-content:space-between;padding:8px 0;font-size:15px}
  .totals-row.discount{color:#10B981}
  .totals-row.total{font-weight:700;font-size:18px;border-top:2px solid #E5E7EB;margin-top:8px;padding-top:16px}
  .payment-badge{display:inline-flex;align-items:center;gap:8px;background:#EDE0F7;color:#5B1E8C;padding:10px 16px;border-radius:8px;font-size:14px;font-weight:500;margin-top:8px}
  .status-badge{display:inline-block;background:#D1FAE5;color:#065F46;padding:6px 14px;border-radius:20px;font-size:13px;font-weight:600}
  .footer{margin-top:40px;padding-top:20px;border-top:1px solid #E5E7EB;text-align:center;color:#9CA3AF;font-size:13px}
  @media print{body{padding:20px}button{display:none}}
</style>
</head>
<body>
<div class="header">
  <div class="logo">
    <div class="logo-box">1</div>
    <span class="logo-text">one</span>
  </div>
  <div class="invoice-meta">
    <h1>INVOICE</h1>
    <p>${invoiceNumber}</p>
    <p>${date}</p>
    <div style="margin-top:8px"><span class="status-badge">✓ Paid</span></div>
  </div>
</div>

<div class="section">
  <h3>Product</h3>
  <div class="product-card">
    <div class="product-icon">📱</div>
    <div style="flex:1">
      <div class="product-name">${product.name}</div>
      <div class="product-brand">${product.brand ?? 'Unknown Brand'}</div>
      ${specs ? `<table class="specs">${specs}</table>` : ''}
    </div>
  </div>
</div>

<div class="section">
  <h3>Order Summary</h3>
  <div class="totals">
    <div class="totals-row">
      <span>Unit price</span>
      <span>${formatPrice(product.price_original ?? 0)}</span>
    </div>
    <div class="totals-row">
      <span>Quantity</span>
      <span>× ${quantity}</span>
    </div>
    <div class="totals-row">
      <span>Subtotal</span>
      <span>${formatPrice(subtotal)}</span>
    </div>
    ${discount > 0 ? `<div class="totals-row discount">
      <span>Discount (${product.discount_percentage}%)</span>
      <span>− ${formatPrice(discount)}</span>
    </div>` : ''}
    <div class="totals-row total">
      <span>Total</span>
      <span>${formatPrice(total)}</span>
    </div>
  </div>
</div>

<div class="section">
  <h3>Payment Method</h3>
  <div class="payment-badge">
    ${paymentMethod === 'prepaid' ? '💳' : '📅'} ${payLabel}
  </div>
</div>

${product.reasoning ? `<div class="section">
  <h3>Your Personalised Offer</h3>
  <div style="background:#EDE0F7;border-radius:10px;padding:14px 18px;font-size:14px;color:#5B1E8C">
    ✨ ${product.reasoning}
  </div>
</div>` : ''}

<div class="footer">
  <p>One Albania Sh.p.k. — Rr. Dritan Hoxha, Tiranë, Albania</p>
  <p style="margin-top:4px">Customer Care: +355 4 XXXXXXX • one.al</p>
  <p style="margin-top:12px;font-size:11px">Thank you for choosing One Albania. This invoice was automatically generated.</p>
</div>
</body>
</html>`;
}
