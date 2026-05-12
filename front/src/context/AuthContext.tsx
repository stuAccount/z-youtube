import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { api } from "../api/endpoints";
import type { LoginResponse, SelfProfile } from "../api/types";

interface AuthContextValue {
  user: SelfProfile | LoginResponse | null;
  loading: boolean;
  refreshUser: () => Promise<void>;
  login: (loginId: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  setUser: (user: SelfProfile | LoginResponse | null) => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<SelfProfile | LoginResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const refreshUser = useCallback(async () => {
    try {
      await api.me();
      const profile = await api.selfProfile();
      setUser(profile);
    } catch {
      setUser(null);
    }
  }, []);

  useEffect(() => {
    refreshUser().finally(() => setLoading(false));
  }, [refreshUser]);

  const login = useCallback(async (loginId: string, password: string) => {
    const account = await api.login({ loginId, password });
    setUser(account);
  }, []);

  const register = useCallback(async (username: string, email: string, password: string) => {
    const account = await api.register({ username, email, password });
    setUser(account);
  }, []);

  const logout = useCallback(async () => {
    await api.logout();
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({ user, loading, refreshUser, login, register, logout, setUser }),
    [user, loading, refreshUser, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return context;
}
