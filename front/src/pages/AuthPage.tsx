import { LogIn, UserPlus } from "lucide-react";
import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { errorMessage } from "../utils/format";

export function AuthPage() {
  const [mode, setMode] = useState<"login" | "register">("login");
  const [loginId, setLoginId] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [busy, setBusy] = useState(false);
  const { login, register } = useAuth();
  const navigate = useNavigate();

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setBusy(true);
    setMessage("");
    try {
      if (mode === "login") {
        await login(loginId, password);
      } else {
        await register(username, email, password);
      }
      navigate("/me");
    } catch (err) {
      setMessage(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <section className="auth-page">
      <div className="auth-panel">
        <div className="segmented">
          <button className={mode === "login" ? "active" : ""} type="button" onClick={() => setMode("login")}>
            <LogIn size={16} /> 登录
          </button>
          <button className={mode === "register" ? "active" : ""} type="button" onClick={() => setMode("register")}>
            <UserPlus size={16} /> 注册
          </button>
        </div>
        <h1>{mode === "login" ? "进入你的频道" : "创建频道账号"}</h1>
        <form className="form-grid" onSubmit={submit}>
          {mode === "login" ? (
            <label>
              账号
              <input value={loginId} onChange={(event) => setLoginId(event.target.value)} placeholder="用户名或邮箱" required />
            </label>
          ) : (
            <>
              <label>
                用户名
                <input value={username} onChange={(event) => setUsername(event.target.value)} required />
              </label>
              <label>
                邮箱
                <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
              </label>
            </>
          )}
          <label>
            密码
            <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required />
          </label>
          {message && <p className="form-error">{message}</p>}
          <button className="primary-button" type="submit" disabled={busy}>
            {busy ? "处理中..." : mode === "login" ? "登录" : "注册"}
          </button>
        </form>
      </div>
    </section>
  );
}
