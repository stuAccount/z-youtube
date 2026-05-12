import {
  Check,
  ChevronRight,
  Heart,
  Home,
  Languages,
  LogIn,
  LogOut,
  MoonStar,
  MoreVertical,
  Search,
  SunMedium,
  UserRound,
  Video,
} from "lucide-react";
import { FormEvent, useEffect, useRef, useState } from "react";
import { Link, NavLink, Outlet, useNavigate, useSearchParams } from "react-router-dom";
import { API_BASE_URL } from "../api/client";
import { api } from "../api/endpoints";
import { useAuth } from "../context/AuthContext";
import { usePreferences } from "../context/PreferencesContext";

export function Layout() {
  const { user, logout } = useAuth();
  const { theme, language, setTheme, setLanguage, t } = usePreferences();
  const brandImageUrl = "/youtube.svg";
  const [searchParams] = useSearchParams();
  const [keyword, setKeyword] = useState(searchParams.get("q") ?? "");
  const [apiStatus, setApiStatus] = useState<"checking" | "online" | "offline">("checking");
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [submenu, setSubmenu] = useState<null | "theme" | "language">(null);
  const navigate = useNavigate();
  const menuRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    setKeyword(searchParams.get("q") ?? "");
  }, [searchParams]);

  useEffect(() => {
    api
      .ping()
      .then(() => setApiStatus("online"))
      .catch(() => setApiStatus("offline"));
  }, []);

  useEffect(() => {
    const handleClick = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setSettingsOpen(false);
        setSubmenu(null);
      }
    };

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        if (submenu) {
          setSubmenu(null);
          return;
        }
        setSettingsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClick);
    document.addEventListener("keydown", handleEscape);
    return () => {
      document.removeEventListener("mousedown", handleClick);
      document.removeEventListener("keydown", handleEscape);
    };
  }, [submenu]);

  const submitSearch = (event: FormEvent) => {
    event.preventDefault();
    const q = keyword.trim();
    navigate(q ? `/?q=${encodeURIComponent(q)}` : "/");
  };

  const themeLabel =
    theme === "dark" ? t("暗色", "Dark") : theme === "light" ? t("浅色", "Light") : t("跟随设备", "Device");
  const languageLabel = language === "zh-CN" ? "中文" : "English";

  return (
    <div className="shell">
      <header className="topbar">
        <Link to="/" className="brand" aria-label="Z-YouTube 首页">
          <span className="brand-mark">
            <img src={brandImageUrl} alt="" aria-hidden="true" />
          </span>
          <span>Z-YouTube</span>
        </Link>

        <form className="searchbar" onSubmit={submitSearch}>
          <input
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="搜索公开视频"
            aria-label={t("搜索公开视频", "Search public videos")}
          />
          <button type="submit" title={t("搜索", "Search")}>
            <Search size={18} />
          </button>
        </form>

        <div className="top-actions">
          <span className={`connection ${apiStatus}`}>
            {apiStatus === "checking"
              ? t("检查连接", "Checking API")
              : apiStatus === "online"
                ? t("API 在线", "API online")
                : t("API 离线", "API offline")}
          </span>
          <div className="settings-anchor" ref={menuRef}>
            <button
              className={`icon-button settings-trigger ${settingsOpen ? "active" : ""}`}
              type="button"
              title={t("设置", "Settings")}
              aria-label={t("设置", "Settings")}
              onClick={() => {
                setSettingsOpen((open) => !open);
                setSubmenu(null);
              }}
            >
              <MoreVertical size={18} />
            </button>

            {settingsOpen && (
              <div className={`settings-menu ${submenu ? `panel-${submenu}` : "panel-root"}`}>
                {!submenu && (
                  <div className="settings-panel">
                    <button className="settings-row" type="button" onClick={() => setSubmenu("theme")}>
                      <span className="settings-icon">
                        <MoonStar size={18} />
                      </span>
                      <span className="settings-copy">
                        <strong>{t("外观", "Appearance")}</strong>
                        <small>{themeLabel}</small>
                      </span>
                      <ChevronRight size={16} />
                    </button>

                    <button className="settings-row" type="button" onClick={() => setSubmenu("language")}>
                      <span className="settings-icon">
                        <Languages size={18} />
                      </span>
                      <span className="settings-copy">
                        <strong>{t("显示语言", "Display language")}</strong>
                        <small>{languageLabel}</small>
                      </span>
                      <ChevronRight size={16} />
                    </button>
                  </div>
                )}

                {submenu === "theme" && (
                  <div className="settings-subpanel">
                    <button className="settings-back" type="button" onClick={() => setSubmenu(null)}>
                      <ChevronRight size={16} className="reverse" />
                      {t("外观", "Appearance")}
                    </button>
                    <p className="settings-note">{t("仅应用到当前浏览器", "Setting applies to this browser only")}</p>
                    <button className={`settings-choice ${theme === "device" ? "selected" : ""}`} type="button" onClick={() => setTheme("device")}>
                      <span className="settings-choice-main">
                        <SunMedium size={17} />
                        <span>{t("跟随设备", "Use device theme")}</span>
                      </span>
                      {theme === "device" && <Check size={16} />}
                    </button>
                    <button className={`settings-choice ${theme === "dark" ? "selected" : ""}`} type="button" onClick={() => setTheme("dark")}>
                      <span className="settings-choice-main">
                        <MoonStar size={17} />
                        <span>{t("深色", "Dark theme")}</span>
                      </span>
                      {theme === "dark" && <Check size={16} />}
                    </button>
                    <button className={`settings-choice ${theme === "light" ? "selected" : ""}`} type="button" onClick={() => setTheme("light")}>
                      <span className="settings-choice-main">
                        <SunMedium size={17} />
                        <span>{t("浅色", "Light theme")}</span>
                      </span>
                      {theme === "light" && <Check size={16} />}
                    </button>
                  </div>
                )}

                {submenu === "language" && (
                  <div className="settings-subpanel">
                    <button className="settings-back" type="button" onClick={() => setSubmenu(null)}>
                      <ChevronRight size={16} className="reverse" />
                      {t("显示语言", "Display language")}
                    </button>
                    <button
                      className={`settings-choice ${language === "zh-CN" ? "selected" : ""}`}
                      type="button"
                      onClick={() => setLanguage("zh-CN")}
                    >
                      <span className="settings-choice-main">
                        <Languages size={17} />
                        <span>中文</span>
                      </span>
                      {language === "zh-CN" && <Check size={16} />}
                    </button>
                    <button className={`settings-choice ${language === "en" ? "selected" : ""}`} type="button" onClick={() => setLanguage("en")}>
                      <span className="settings-choice-main">
                        <Languages size={17} />
                        <span>English</span>
                      </span>
                      {language === "en" && <Check size={16} />}
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
          {user ? (
            <>
              <Link className="avatar-link" to="/me" title={t("我的空间", "My space")}>
                {avatarText(user.nickname || user.username)}
              </Link>
              <button className="icon-button" type="button" onClick={() => logout()} title={t("退出登录", "Sign out")}>
                <LogOut size={18} />
              </button>
            </>
          ) : (
            <Link className="pill-button" to="/auth">
              <LogIn size={16} />
              {t("登录", "Sign in")}
            </Link>
          )}
        </div>
      </header>

      <aside className="sidebar">
        <NavLink to="/" end>
          <Home size={19} />
          {t("首页", "Home")}
        </NavLink>
        <NavLink to="/studio">
          <Video size={19} />
          {t("创作者", "Studio")}
        </NavLink>
        <NavLink to="/me">
          <Heart size={19} />
          {t("收藏", "Favorites")}
        </NavLink>
        <NavLink to="/me">
          <UserRound size={19} />
          {t("我的", "Me")}
        </NavLink>
      </aside>

      <main className="content">
        {!API_BASE_URL && (
          <div className="config-warning">
            {t("当前未配置 ", "Missing ")}
            <code>VITE_API_BASE_URL</code>
            {t("，将按同源地址请求后端。", ". Requests will use the current origin.")}
          </div>
        )}
        <Outlet />
      </main>
    </div>
  );
}

function avatarText(name: string) {
  return (name || "Z").trim().slice(0, 1).toUpperCase() || "Z";
}
