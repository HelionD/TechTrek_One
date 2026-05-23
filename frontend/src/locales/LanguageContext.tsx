import { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';
import type { Language, Translations } from './translations';
import { getTranslation } from './translations';

interface LanguageContextType {
    language: Language;
    setLanguage: (lang: Language) => void;
    t: Translations;
}

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

export function LanguageProvider({ children }: { children: ReactNode }) {
    const [language, setLanguageState] = useState<Language>(() => {
        // Try to get from localStorage
        const saved = localStorage.getItem('lang_preference') as Language | null;
        return saved && (saved === 'AL' || saved === 'EN') ? saved : 'AL';
    });

    const setLanguage = (lang: Language) => {
        setLanguageState(lang);
        localStorage.setItem('lang_preference', lang);
    };

    return (
        <LanguageContext.Provider value={{ language, setLanguage, t: getTranslation(language) }}>
            {children}
        </LanguageContext.Provider>
    );
}

export function useLanguage() {
    const context = useContext(LanguageContext);
    if (!context) {
        throw new Error('useLanguage must be used within a LanguageProvider');
    }
    return context;
}
