import { useState } from 'react';
import { X, CreditCard, Calendar, Lock, User, CheckCircle, AlertCircle } from 'lucide-react';
import type { Product, PaymentMethod, CardDetails } from '../types';
import { generateInvoicePDF } from '../utils/invoice';
import './PaymentModal.css';

interface PaymentModalProps {
  product: Product;
  method: PaymentMethod;
  onClose: () => void;
  onSuccess: () => void;
}

type Step = 'card' | 'processing' | 'success';

export default function PaymentModal({ product, method, onClose, onSuccess }: PaymentModalProps) {
  const [step, setStep] = useState<Step>('card');
  const [card, setCard] = useState<CardDetails>({ cardNumber: '', cardHolder: '', expiry: '', cvv: '' });
  const [errors, setErrors] = useState<Partial<CardDetails>>({});

  const price = product.final_price ?? product.price_original ?? 0;
  const discount = product.price_original && product.final_price
    ? product.price_original - product.final_price
    : 0;

  function formatCardNumber(value: string) {
    return value.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim();
  }

  function formatExpiry(value: string) {
    const digits = value.replace(/\D/g, '').slice(0, 4);
    return digits.length >= 3 ? `${digits.slice(0, 2)}/${digits.slice(2)}` : digits;
  }

  function validate() {
    const nextErrors: Partial<CardDetails> = {};
    const number = card.cardNumber.replace(/\s/g, '');
    if (number.length < 16) nextErrors.cardNumber = 'Enter a valid 16-digit card number';
    if (!card.cardHolder.trim()) nextErrors.cardHolder = 'Cardholder name is required';
    if (card.expiry.length < 5) nextErrors.expiry = 'Enter expiry in MM/YY format';
    if (card.cvv.length < 3) nextErrors.cvv = 'Enter your 3-digit CVV';
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  async function handlePay() {
    if (!validate()) return;
    setStep('processing');
    await new Promise((resolve) => setTimeout(resolve, 2000));
    setStep('success');
    const invoice = {
      invoiceNumber: 'INV-' + Date.now().toString(36).toUpperCase(),
      date: new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' }),
      product,
      quantity: 1,
      subtotal: product.price_original ?? price,
      discount,
      total: price,
      paymentMethod: method,
      cardLast4: card.cardNumber.replace(/\s/g, '').slice(-4),
    };
    setTimeout(() => generateInvoicePDF(invoice), 800);
  }

  function handlePostpayConfirm() {
    setStep('processing');
    setTimeout(() => {
      setStep('success');
      const invoice = {
        invoiceNumber: 'BILL-' + Date.now().toString(36).toUpperCase(),
        date: new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' }),
        product,
        quantity: 1,
        subtotal: product.price_original ?? price,
        discount,
        total: price,
        paymentMethod: method,
      };
      setTimeout(() => generateInvoicePDF(invoice), 800);
    }, 1800);
  }

  return (
    <div className="modal-backdrop" onClick={(event) => { if (event.target === event.currentTarget && step !== 'processing') onClose(); }}>
      <div className="modal payment-modal">
        <div className="pm-header">
          <div className="pm-logo">
            <span className="pm-logo-box">1</span>
            <span>one</span>
          </div>
          {step !== 'processing' && (
            <button className="pm-close" onClick={onClose} aria-label="Close"><X size={20} /></button>
          )}
        </div>

        {step === 'card' && method === 'prepaid' && (
          <div className="slide-up">
            <h2 className="pm-title">Card Payment</h2>
            <p className="pm-subtitle">Enter your card details to complete the purchase</p>

            <div className="pm-summary">
              <span className="pm-summary-name">{product.name}</span>
              <span className="pm-summary-price">{price.toLocaleString('sq-AL')} L</span>
            </div>
            {discount > 0 && (
              <div className="pm-discount-note">
                ✓ Saving {discount.toLocaleString('sq-AL')} L with your personalised discount
              </div>
            )}

            <div className="pm-form">
              <div className="pm-field">
                <label>Card Number</label>
                <div className={`pm-input-wrap ${errors.cardNumber ? 'error' : ''}`}>
                  <CreditCard size={16} className="pm-input-icon" />
                  <input
                    type="text"
                    inputMode="numeric"
                    placeholder="1234 5678 9012 3456"
                    value={card.cardNumber}
                    maxLength={19}
                    onChange={(event) => {
                      setCard((prev) => ({ ...prev, cardNumber: formatCardNumber(event.target.value) }));
                      setErrors((next) => ({ ...next, cardNumber: undefined }));
                    }}
                  />
                </div>
                {errors.cardNumber && <span className="pm-error"><AlertCircle size={12} />{errors.cardNumber}</span>}
              </div>

              <div className="pm-field">
                <label>Cardholder Name</label>
                <div className={`pm-input-wrap ${errors.cardHolder ? 'error' : ''}`}>
                  <User size={16} className="pm-input-icon" />
                  <input
                    type="text"
                    placeholder="ANNA SMITH"
                    value={card.cardHolder}
                    style={{ textTransform: 'uppercase' }}
                    onChange={(event) => {
                      setCard((prev) => ({ ...prev, cardHolder: event.target.value.toUpperCase() }));
                      setErrors((next) => ({ ...next, cardHolder: undefined }));
                    }}
                  />
                </div>
                {errors.cardHolder && <span className="pm-error"><AlertCircle size={12} />{errors.cardHolder}</span>}
              </div>

              <div className="pm-row">
                <div className="pm-field">
                  <label>Expiry Date</label>
                  <div className={`pm-input-wrap ${errors.expiry ? 'error' : ''}`}>
                    <Calendar size={16} className="pm-input-icon" />
                    <input
                      type="text"
                      inputMode="numeric"
                      placeholder="MM/YY"
                      value={card.expiry}
                      maxLength={5}
                      onChange={(event) => {
                        setCard((prev) => ({ ...prev, expiry: formatExpiry(event.target.value) }));
                        setErrors((next) => ({ ...next, expiry: undefined }));
                      }}
                    />
                  </div>
                  {errors.expiry && <span className="pm-error"><AlertCircle size={12} />{errors.expiry}</span>}
                </div>

                <div className="pm-field">
                  <label>CVV</label>
                  <div className={`pm-input-wrap ${errors.cvv ? 'error' : ''}`}>
                    <Lock size={16} className="pm-input-icon" />
                    <input
                      type="password"
                      inputMode="numeric"
                      placeholder="•••"
                      maxLength={4}
                      value={card.cvv}
                      onChange={(event) => {
                        setCard((prev) => ({ ...prev, cvv: event.target.value.replace(/\D/g, '') }));
                        setErrors((next) => ({ ...next, cvv: undefined }));
                      }}
                    />
                  </div>
                  {errors.cvv && <span className="pm-error"><AlertCircle size={12} />{errors.cvv}</span>}
                </div>
              </div>
            </div>

            <div className="pm-secure-note">
              <Lock size={12} /> Secured with 256-bit SSL encryption
            </div>

            <button className="pm-pay-btn" onClick={handlePay}>
              Pay {price.toLocaleString('sq-AL')} L
            </button>
          </div>
        )}

        {step === 'card' && method === 'postpaid' && (
          <div className="slide-up">
            <h2 className="pm-title">Add to Monthly Bill</h2>
            <p className="pm-subtitle">This will be charged with your next monthly phone bill</p>

            <div className="pm-postpay-card">
              <div className="pm-postpay-icon">📅</div>
              <div>
                <div className="pm-postpay-label">Postpaid Billing</div>
                <div className="pm-postpay-desc">Amount will appear on your next invoice at the end of the month.</div>
              </div>
            </div>

            <div className="pm-summary" style={{ marginTop: 16 }}>
              <span className="pm-summary-name">{product.name}</span>
              <span className="pm-summary-price">{price.toLocaleString('sq-AL')} L</span>
            </div>
            {discount > 0 && (
              <div className="pm-discount-note">
                ✓ Saving {discount.toLocaleString('sq-AL')} L with your personalised discount
              </div>
            )}

            <div className="pm-postpay-terms">
              By confirming, you agree to the One Albania Postpaid Purchase Terms. The amount will be included in your next bill cycle.
            </div>

            <button className="pm-pay-btn pm-pay-btn--postpay" onClick={handlePostpayConfirm}>
              Confirm & Add to Bill
            </button>
          </div>
        )}

        {step === 'processing' && (
          <div className="pm-processing slide-up">
            <div className="pm-spinner" />
            <p className="pm-processing-text">
              {method === 'prepaid' ? 'Processing your payment…' : 'Adding to your bill…'}
            </p>
            <p className="pm-processing-sub">Please do not close this window</p>
          </div>
        )}

        {step === 'success' && (
          <div className="pm-success slide-up">
            <div className="pm-success-icon">
              <CheckCircle size={56} strokeWidth={1.5} />
            </div>
            <h2 className="pm-success-title">
              {method === 'prepaid' ? 'Payment Successful!' : 'Added to Your Bill!'}
            </h2>
            <p className="pm-success-sub">
              {method === 'prepaid'
                ? 'Your invoice has been generated and is printing…'
                : 'This purchase will appear on your next monthly statement.'}
            </p>
            <div className="pm-success-detail">
              <span>Amount</span>
              <span className="pm-success-amount">{price.toLocaleString('sq-AL')} L</span>
            </div>
            <button className="pm-pay-btn pm-pay-btn--done" onClick={() => { onSuccess(); onClose(); }}>
              Done
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
