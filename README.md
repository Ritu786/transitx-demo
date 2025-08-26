# TransitX Demo (RTA-like)

Tiny Spring Boot app with intentional vulnerabilities (toggle with `VULN_MODE=on`) to demo Synopsys:
- Coverity SAST
- Black Duck SCA
- Seeker IAST
- SRM Policy Gate

## Run locally

```bash
cd backend
export VULN_MODE=on   # enable intentional vulns
mvn -q spring-boot:run
# poke endpoints:
curl 'http://localhost:8080/api/fines/lookup?plate=A12345'
curl -H 'X-User: alice' 'http://localhost:8080/api/wallet/alice'
curl 'http://localhost:8080/api/trip/search?from=Deira&to=Marina&q=<script>alert(1)</script>'
