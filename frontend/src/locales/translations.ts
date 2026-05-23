export type Language = 'AL' | 'EN';

export interface Translations {
    // Header
    header: {
        individual: string;
        business: string;
        company: string;
        search: string;
        account: string;
    };
    // Navigation
    nav: {
        mobile: string;
        internetTV: string;
        eShop: string;
        customerCare: string;
    };
    // Logo
    logo: string;
    // Hero section
    hero: {
        eyebrow: string;
        title: string;
        description: string;
        browseProducts: string;
        shopNow: string;
    };
    // Hero panel
    heroPanel: {
        label: string;
        title: string;
        featuredDevices: string;
        paymentOptions: string;
        instantInvoice: string;
    };
    // Shop section
    shop: {
        label: string;
        title: string;
        filterSort: string;
        noProducts: string;
        tryAdjustFilters: string;
    };
    // Filter & Sort
    filter: {
        sortBy: string;
        recommended: string;
        priceLowToHigh: string;
        priceHighToLow: string;
        newest: string;
        priceRange: string;
        allPrices: string;
        under100k: string;
        range100_300k: string;
        range300_500k: string;
        above500k: string;
    };
    // Product card
    product: {
        personalised: string;
        viewDetails: string;
    };
    // Cart
    cart: {
        title: string;
        empty: string;
        continueShopping: string;
        total: string;
        note: string;
        quantity: string;
        remove: string;
    };
    // Payment
    payment: {
        cardPayment: string;
        enterDetails: string;
        cardNumber: string;
        cardholder: string;
        expiryDate: string;
        cvv: string;
        secured: string;
        pay: string;
        postpay: string;
        confirm: string;
        billing: string;
        successTitle: string;
        successDesc: string;
        invoiceDownload: string;
        cardLast4: string;
    };
    // Product Detail
    productDetail: {
        specs: string;
        addToCart: string;
        addToWishlist: string;
        inStock: string;
        outOfStock: string;
    };
    // Status messages
    status: {
        loading: string;
        processing: string;
        success: string;
        error: string;
        offline: string;
    };
}

export const translations: Record<Language, Translations> = {
    AL: {
        header: {
            individual: 'Individ',
            business: 'Biznes',
            company: 'Kompani',
            search: 'Kërko',
            account: 'Llogaria',
        },
        nav: {
            mobile: 'Mobile',
            internetTV: 'Internet & TV',
            eShop: 'E-Shop',
            customerCare: 'Shërbim Klienti',
        },
        logo: 'one',
        hero: {
            eyebrow: 'One Albania E-Shop',
            title: 'Zbuloni telefonat dhe pajisjet e ardhshme me pagesa para ose pas',
            description: 'Shfleto pajisjet më të fundit, merrni çmime të personalizuara, dhe gjenero faturën menjëherë pas blerjes.',
            browseProducts: 'Shfletoni produktet',
            shopNow: 'Bleni tani',
        },
        heroPanel: {
            label: 'Ofertat më të mira të ditës',
            title: 'Bëni pagesën e telefonit ose pajisjes tuaj të ardhshme të thjeshtë.',
            featuredDevices: 'Pajisje të zgjedhura',
            paymentOptions: 'Opsione pagese',
            instantInvoice: 'Faturë e menjëhershme',
        },
        shop: {
            label: 'Vetëm në E-Shop',
            title: 'Zgjidhni një pajisje',
            filterSort: 'Filter & Rendez',
            noProducts: 'Nuk u gjet asnjë produkt',
            tryAdjustFilters: 'Provoni të rregulloni filtrat ose rifreskoni faqen.',
        },
        filter: {
            sortBy: 'Rendez sipas',
            recommended: 'E rekomanduar',
            priceLowToHigh: 'Çmim: I ulët në të lartë',
            priceHighToLow: 'Çmim: I lartë në të ulët',
            newest: 'Më i ri',
            priceRange: 'Gama e çmimit',
            allPrices: 'Të gjithë çmimet',
            under100k: 'Nën 100,000 L',
            range100_300k: '100,000 - 300,000 L',
            range300_500k: '300,000 - 500,000 L',
            above500k: 'Mbi 500,000 L',
        },
        product: {
            personalised: '✨ Oferta e personalizuar',
            viewDetails: 'Shikoni detajet →',
        },
        cart: {
            title: 'Shporta',
            empty: 'Shporta juaj është bosh',
            continueShopping: 'Vazhdoni blerjen',
            total: 'Totali',
            note: 'Hyni në checkout të produktit individual duke përdorur Para Pay ose Post Pay',
            quantity: 'Sasia',
            remove: 'Hiq',
        },
        payment: {
            cardPayment: 'Pagesa me kartë',
            enterDetails: 'Futni detajet e kartës suaj për të përfunduar blerjen',
            cardNumber: 'Numri i kartës',
            cardholder: 'Emri në kartë',
            expiryDate: 'Data e përfundimit',
            cvv: 'CVV',
            secured: 'Siguruar me enkriptim SSL 256-bit',
            pay: 'Paguaj',
            postpay: 'Postpay',
            confirm: 'Konfirmo',
            billing: 'Faturim',
            successTitle: 'Pagesa e suksesshme!',
            successDesc: 'Fatura juaj u gjenera. Shkarko ose kontrollo email-in tuaj.',
            invoiceDownload: 'Shkarko faturën',
            cardLast4: 'Katër shifrat e fundit',
        },
        productDetail: {
            specs: 'Specifikat',
            addToCart: 'Shto në shportë',
            addToWishlist: 'Shto në favoritë',
            inStock: 'Në stok',
            outOfStock: 'Mbaroi stoqet',
        },
        status: {
            loading: 'Po ngarkohet…',
            processing: 'Po përpunohet…',
            success: 'Suksese!',
            error: 'Ka ndodhur një gabim',
            offline: 'Offline',
        },
    },
    EN: {
        header: {
            individual: 'Individual',
            business: 'Business',
            company: 'Company',
            search: 'Search',
            account: 'Account',
        },
        nav: {
            mobile: 'Mobile',
            internetTV: 'Internet & TV',
            eShop: 'E-Shop',
            customerCare: 'Customer Care',
        },
        logo: 'one',
        hero: {
            eyebrow: 'One Albania E-Shop',
            title: 'Discover phones and wearables with prepay or postpay checkout.',
            description: 'Browse the latest devices, get personalised prices, and generate an invoice instantly after checkout.',
            browseProducts: 'Browse products',
            shopNow: 'Shop now',
        },
        heroPanel: {
            label: "Today's best deals",
            title: 'Make your next phone or wearable payment simple.',
            featuredDevices: 'Featured devices',
            paymentOptions: 'Payment options',
            instantInvoice: 'Instant invoice',
        },
        shop: {
            label: 'E-Shop only',
            title: 'Select a device',
            filterSort: 'Filter & Sort',
            noProducts: 'No products found',
            tryAdjustFilters: 'Try adjusting your filters or refresh the page.',
        },
        filter: {
            sortBy: 'Sort By',
            recommended: 'Recommended',
            priceLowToHigh: 'Price: Low to High',
            priceHighToLow: 'Price: High to Low',
            newest: 'Newest',
            priceRange: 'Price Range',
            allPrices: 'All Prices',
            under100k: 'Under 100,000 L',
            range100_300k: '100,000 - 300,000 L',
            range300_500k: '300,000 - 500,000 L',
            above500k: 'Above 500,000 L',
        },
        product: {
            personalised: '✨ Personalised offer',
            viewDetails: 'View Details →',
        },
        cart: {
            title: 'Cart',
            empty: 'Your cart is empty',
            continueShopping: 'Continue Shopping',
            total: 'Total',
            note: 'Proceed to individual product checkout using Pre Pay or Post Pay',
            quantity: 'Qty',
            remove: 'Remove',
        },
        payment: {
            cardPayment: 'Card Payment',
            enterDetails: 'Enter your card details to complete the purchase',
            cardNumber: 'Card Number',
            cardholder: 'Cardholder Name',
            expiryDate: 'Expiry Date',
            cvv: 'CVV',
            secured: 'Secured with 256-bit SSL encryption',
            pay: 'Pay',
            postpay: 'Postpay',
            confirm: 'Confirm',
            billing: 'Billing',
            successTitle: 'Payment Successful!',
            successDesc: 'Your invoice has been generated. Download or check your email.',
            invoiceDownload: 'Download Invoice',
            cardLast4: 'last 4 digits',
        },
        productDetail: {
            specs: 'Specifications',
            addToCart: 'Add to Cart',
            addToWishlist: 'Add to Wishlist',
            inStock: 'In Stock',
            outOfStock: 'Out of Stock',
        },
        status: {
            loading: 'Loading products…',
            processing: 'Processing…',
            success: 'Success!',
            error: 'An error occurred',
            offline: 'Offline',
        },
    },
};

export function getTranslation(language: Language): Translations {
    return translations[language];
}
