const $ = (id) => document.getElementById(id);

function show(pre, value) {
  try {
    const txt = typeof value === "string" ? value : JSON.stringify(value, null, 2);
    pre.textContent = txt;
  } catch {
    pre.textContent = String(value);
  }
}

/* Wallet */
$("walletForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const user = $("walletUser").value.trim() || "alice";
  try {
    const res = await fetch(`/api/wallet/${encodeURIComponent(user)}`, {
      headers: { "X-User": user }
    });
    const data = await res.text();
    show($("walletOut"), data);
  } catch (err) {
    show($("walletOut"), `Error: ${err}`);
  }
});

/* Fines */
$("finesForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const plate = $("plate").value.trim() || "A12345";
  try {
    const res = await fetch(`/api/fines/lookup?plate=${encodeURIComponent(plate)}`);
    const data = await res.text();
    show($("finesOut"), data);
  } catch (err) {
    show($("finesOut"), `Error: ${err}`);
  }
});

/* Trips (includes XSS payload support) */
$("tripForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const from = $("from").value || "Deira";
  const to = $("to").value || "Marina";
  const q = $("q").value || "normal";
  try {
    const url = `/api/trip/search?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&q=${encodeURIComponent(q)}`;
    const res = await fetch(url);
    const data = await res.text();
    show($("tripOut"), data);
  } catch (err) {
    show($("tripOut"), `Error: ${err}`);
  }
});

/* Parking upload (path traversal if VULN_MODE=on) */
$("uploadForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const fileInput = $("uploadFile");
  if (!fileInput.files.length) return show($("uploadOut"), "Pick a file first.");
  const f = fileInput.files[0];
  const overrideName = $("overrideName").value.trim();

  // If user typed a custom name (e.g., ../evil.txt), wrap the file so backend might honor it
  const filename = overrideName || f.name;
  const blob = new File([f], filename, { type: f.type });

  const form = new FormData();
  form.append("file", blob, filename);

  try {
    const res = await fetch("/api/parking/uploadDoc", { method: "POST", body: form });
    const txt = await res.text();
    show($("uploadOut"), txt);
  } catch (err) {
    show($("uploadOut"), `Error: ${err}`);
  }
});

/* Health */
$("checkHealth").addEventListener("click", async () => {
  try {
    const res = await fetch("/actuator/health");
    show($("healthOut"), await res.text());
  } catch (err) {
    show($("healthOut"), `Error: ${err}`);
  }
});

/* Traffic generator – 25 requests with mixed payloads */
$("goTraffic").addEventListener("click", async () => {
  const out = $("trafficOut");
  out.textContent = "Starting traffic burst...\n";
  const payloads = [
    "<script>alert(1)</script>",
    "' OR '1'='1",
    "../../etc/passwd",
    "<img src=x onerror=alert(2)>",
    "%3Cscript%3Ealert(3)%3C/script%3E"
  ];
  const from = ["Deira", "Bur Dubai", "Airport"];
  const to = ["Marina", "JLT", "Business Bay"];

  const sleep = (ms) => new Promise(r => setTimeout(r, ms));

  for (let i = 0; i < 25; i++) {
    const q = payloads[i % payloads.length];
    const f = from[i % from.length];
    const t = to[i % to.length];
    try {
      // Trip search (reflected XSS vector)
      await fetch(`/api/trip/search?from=${encodeURIComponent(f)}&to=${encodeURIComponent(t)}&q=${encodeURIComponent(q)}`);

      // Fines lookup
      await fetch(`/api/fines/lookup?plate=A12345`);

      // Wallet (header-based user)
      const user = i % 2 === 0 ? "alice" : "bob";
      await fetch(`/api/wallet/${encodeURIComponent(user)}`, { headers: { "X-User": user } });

      // Small delay so traffic is observable
      out.textContent += `Sent batch ${i + 1}/25\n`;
      await sleep(250);
    } catch (err) {
      out.textContent += `Error on batch ${i + 1}: ${err}\n`;
    }
  }
  out.textContent += "Traffic burst complete.";
});
