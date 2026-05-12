import { createContext, useContext, useEffect, useState } from "react";

type ThemeMode = "light" | "dark" | "device";
type Language = "zh-CN" | "en";

interface PreferencesContextValue {
  theme: ThemeMode;
  language: Language;
  setTheme: (theme: ThemeMode) => void;
  setLanguage: (language: Language) => void;
  t: (zh: string, en: string) => string;
}

const THEME_KEY = "z-youtube-theme";
const LANGUAGE_KEY = "z-youtube-language";

const PreferencesContext = createContext<PreferencesContextValue | null>(null);

function readTheme() {
  const saved = localStorage.getItem(THEME_KEY);
  return saved === "light" || saved === "dark" || saved === "device" ? saved : "device";
}

function readLanguage() {
  const saved = localStorage.getItem(LANGUAGE_KEY);
  return saved === "en" || saved === "zh-CN" ? saved : "zh-CN";
}

function applyTheme(theme: ThemeMode) {
  const root = document.documentElement;
  const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
  const resolved = theme === "device" ? (prefersDark ? "dark" : "light") : theme;
  root.dataset.theme = resolved;
}

export function PreferencesProvider({ children }: { children: React.ReactNode }) {
  const [theme, setThemeState] = useState<ThemeMode>(() => readTheme());
  const [language, setLanguageState] = useState<Language>(() => readLanguage());

  useEffect(() => {
    applyTheme(theme);
    localStorage.setItem(THEME_KEY, theme);

    if (theme !== "device") return;
    const media = window.matchMedia("(prefers-color-scheme: dark)");
    const handler = () => applyTheme("device");
    media.addEventListener("change", handler);
    return () => media.removeEventListener("change", handler);
  }, [theme]);

  useEffect(() => {
    localStorage.setItem(LANGUAGE_KEY, language);
    document.documentElement.lang = language === "zh-CN" ? "zh-CN" : "en";
  }, [language]);

  return (
    <PreferencesContext.Provider
      value={{
        theme,
        language,
        setTheme: setThemeState,
        setLanguage: setLanguageState,
        t: (zh, en) => (language === "zh-CN" ? zh : en),
      }}
    >
      {children}
    </PreferencesContext.Provider>
  );
}

export function usePreferences() {
  const context = useContext(PreferencesContext);
  if (!context) {
    throw new Error("usePreferences must be used inside PreferencesProvider");
  }
  return context;
}
