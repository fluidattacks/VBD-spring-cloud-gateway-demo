# VBD-spring-cloud-gateway-demo: synthetic vulnerability benchmark

This fork is a **synthetic, openly labelled benchmark**. It is not the upstream project
and must not be used as one. Each sample is an isolated pair of branches off `baseline`:

- `baseline`: the pinned upstream commit plus any hardening; the common ancestor.
- `smpl/<NNN>/safe`: the genuine negative (validated control) of sample `NNN`.
- `smpl/<NNN>/vuln`: `safe` plus exactly one commit that weakens the control.

The class and the coordinates of every sample live only in `GROUND_TRUTH.csv` on this
orphan branch, never in the analysed branches.

| sample | file | class |
|---|---|---|
| 001 | `src/main/java/com/api/gateway/filters/IpFilter.java` | F115 Security controls bypass or absence (client_trusted_value / trusting a less-trusted source for an IP allow-list; CWE-290, CWE-348) |
| 002 | `src/main/java/com/api/gateway/DemoApplication.java` | F115 Security controls bypass or absence (rate_limit_key_trust / trusting a less-trusted source; CWE-348, CWE-770) |
| 003 | `src/main/java/com/api/gateway/DemoApplication.java` | F115 Security controls bypass or absence (absent_control: rate limiting missing on a route to a rate-limited upstream; CWE-770, CWE-799) |
