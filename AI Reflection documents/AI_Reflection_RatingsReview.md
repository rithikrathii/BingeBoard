# Ratings and Review Service
**Md Farhan Tahmid - 31416** 


## 1. Tools Used
Claude Sonnet, ChatGPT 


## 2. How AI Was Used in This Project
In my Ratings and Review service, I have used AI to research and learn about other microservices, and approaches to connect to them. I also used AI to get Git guidance, and best practices. I did a brief search on how JWT secrets work and how to integrate JWT authentication in my APIs, so that other services, specially authentication service can easily connect to my service smoothly later on, with minimal changes. The major use of AI in my service was to guide me to fix errors, understand error messages & debug, understand different tools (e.g., MongoDB, Docker, Git), rather than coding itself. 

While using AI in the above mentioned cases, I always followed the idea of Zero Trust AI - “Verify before trust”, and keep asking-understanding “Why?”. 

Below are the categories which can give a practical understanding of AI usage in terms of prompts and AI responses.  

### Planning and understanding technical tools to make smooth integration

Before starting with creation of my service, I wanted to understand the technical tools I would be using, and to identify what I need to bear in mind for the overall design. 

**Prompt**
```text
what are things to consider when designing a micro service for an android app, combining with database and docker?
```

**AI response**
```text
API design

RESTful or gRPC endpoints, versioned (e.g. /api/v1/...)
JSON payloads optimized for mobile (avoid over-fetching — consider pagination, partial responses)
Clear auth flow (JWT/OAuth2) since Android clients are untrusted by default

Database

Pick based on access pattern: relational (Postgres/MySQL) for structured/relational data, NoSQL (MongoDB/Redis) for flexible or high-speed caching
Connection pooling — mobile traffic can spike unpredictably
Migrations handled outside the container lifecycle (Flyway/Liquibase) so schema changes don't depend on container restarts

Docker considerations

Separate containers for app service and DB (don't bundle them) — use docker-compose for local dev
Persistent volumes for DB data so container restarts don't wipe state
Environment variables / secrets management (don't bake credentials into images)
Health checks (HEALTHCHECK or readiness probes) so orchestrators know when the service is actually ready
```



**Prompt**
```text
conceptually, how does jwt secret flow between different microservices to verify an authentication?
```

**AI response**
```text
Conceptually, it comes down to who signs vs who verifies:
1. Symmetric approach (shared secret)

One secret key is shared across all microservices.
Auth service signs the JWT with the secret.
Every other microservice has the same secret and uses it to verify the signature.
Simple, but risky — if any service is compromised, the secret (and trust) for the whole system is exposed.

2. Asymmetric approach (public/private key) — more common in microservices

Auth service holds a private key, used to sign JWTs.
Auth service publishes the corresponding public key (often via a /jwks endpoint or shared config).
Other microservices only need the public key to verify the signature — they never see the private key.
This way, only the auth service can issue valid tokens, but every service can verify them independently, without calling the auth service for every request.

Typical flow:

Client logs in → Auth service verifies credentials → signs JWT with private key → returns token.
Client sends JWT in Authorization header on each request to any microservice.
Each microservice independently verifies the JWT signature using the public key (no network call needed — fast, decoupled).
If valid and not expired, the service trusts the claims inside (user ID, roles, etc.) and proceeds.

Key point: verification is usually stateless and local — services don't ask the auth service "is this valid?" each time; they just check the signature themselves using the shared public key (or shared secret in the symmetric case). This is what makes JWT good for distributed/microservice systems — no centralized session lookup needed.
```





### Security aspects and best practices: Managing secrets in docker and Git

Keeping in mind the security aspect of my microservice, I wanted to understand how secrets and keys are managed if I will be using Git and Docker. Therefore I asked AI to give me an idea of security best practices.

**Prompt**
```text
If I plan to use Git and Docker for deploying a micro service, which would also contain JWT tokens, database secrets, and other secrets, how to securely manage them in Git and docker?
```

**AI response**
```text
The golden rule is: secrets never live in Git, and never get baked into Docker images.
In Git:

Never commit .env files, keys, or passwords — add them to .gitignore.
Commit a .env.example (with placeholder values) instead, so others know what's needed.
If a secret accidentally gets committed, rotate it immediately — removing it from Git history isn't enough since it may already be exposed.

In Docker:

Don't hardcode secrets in Dockerfile or docker-compose.yml — pass them in at runtime via environment variables.
Use Docker secrets (in Swarm) or mounted secret files for production, rather than plain env vars when possible — env vars can leak via logs or docker inspect.
Keep secrets out of image layers entirely (a secret used only during build can still remain cached in a layer, so use multi-stage builds or build-time secret mounts).

For actual secret storage:

Use a secrets manager (e.g. AWS Secrets Manager, HashiCorp Vault, Azure Key Vault) or your CI/CD platform's built-in secrets store (GitHub Actions secrets, GitLab CI variables).
These get injected into the container only at deploy/runtime — never stored in the repo or image.

General practice:

Different secrets per environment (dev/staging/prod), so a leak in one doesn't compromise others.
Rotate secrets periodically and immediately after any suspected leak.
Limit who/what has access — least privilege principle.
```




### Planning demo JWT integration in my service

I am responsible for creating the ratings and review service, and Emre is the responsible for authentication service. However, I did not want to make a lot of changes later on to integrate the JWT Secrets and authentication with my service. Therefore I planned to add a demo JWT authentication, which could be later connected with the actual authentication service. 

**Prompt**
```text
in a microservice, how is a JWT authentication carried out? how to coordinate with auth team to validate tokens?
```

**AI response**
```text
Here's how it typically works end-to-end, plus how to coordinate with the auth team:
How JWT auth works in a microservice

Client authenticates — sends credentials to the Auth service (often called "Identity Provider" or "Auth Server").
Auth service issues a JWT — signed with a private key (or shared secret), containing claims like user ID, roles, expiry.
Client attaches JWT to every subsequent request, usually in the Authorization: Bearer <token> header.
Your microservice intercepts the request — typically via middleware/filter, before it hits your business logic.
Middleware verifies the token:

Checks signature validity (using public key or shared secret)
Checks expiry (exp claim)
Optionally checks issuer (iss), audience (aud) to confirm token was meant for your service


If valid, the request proceeds, and claims (user ID, roles) are attached to the request context for downstream use (e.g. authorization checks).
If invalid/expired, the service returns 401 Unauthorized.

This verification is usually local and stateless — your service doesn't call the auth service per-request, it just checks the signature.
Coordinating with the auth team
A few key things to align on upfront:

Signing method: symmetric (shared secret) vs asymmetric (public/private key, e.g. RS256). Asymmetric is usually preferred so you only need their public key, not a shared secret.
Public key distribution: ask if they expose a JWKS endpoint (/.well-known/jwks.json) — this lets your service fetch and cache the public key automatically, and handles key rotation gracefully.
Token structure/claims: get a sample decoded JWT — confirm field names for user ID, roles/permissions, expiry, issuer, audience.
Token lifetime & refresh strategy: how long tokens are valid, whether there's a refresh token flow you need to support.
Revocation strategy: ask how logout/token revocation is handled, since JWTs are stateless by default and can't be "deleted" — common solutions are short expiry + refresh tokens, or a blocklist.
Environment-specific keys/endpoints: confirm separate JWKS URLs or secrets for dev/staging/prod.
```




### Debugging and understanding error messages throughout development

While creating the service, I was faced with errors. For the error messages which I didn’t understand, i prompted the error messages to AI.

**Prompt**
```text
[SSL: CERTIFICATE_VERIFY_FAILED] certificate verify failed: unable to get local issuer certificate
```

**AI response**
```text
Newer Python installs on Mac don't auto-load system SSL certificates.

Fix: Run /Applications/Python\ 3.14/Install\ Certificates.command to install them.
```



**Prompt**
```text
docker exec -it ratings-service curl http://localhost:8001/health
OCI runtime exec failed: ... exec: "curl": executable file not found in $PATH
```



**AI response**
```text
The python:3.12-slim Docker image doesn't include curl by default — that's intentional, it keeps the image small.

Fix: Run curl from your Mac terminal against the mapped port instead of from inside the container.
```



## 3. Reflection

Being allowed to use AI in this project, and then maintaining an AI reflection document gave me new insights on the usage of AI. Instead of letting AI take control, I have been focused on me driving the tasks, and let AI be an assistant for support. I took note of 2 key points in the very beginning:

1. From my previous experience, AI makes mistakes, even simple ones. 
2. From my work, I learnt to be security focused. Therefore I will follow the Zero Trust security framework, to verify and not to blindly trust.

To verify AI answers in critical responses, I used google to verify with official documentation, ask colleagues how they implemented it, and use a second AI for cross checking answers before proceeding. 
AI is of great assistance if it is used in the correct way. And openly allowing AI usage for this course inspired me to learn to use AI as an assistant, instead of letting AI behave autonomously. I could research lots of topics and concepts in a short time, and with lower efforts, compared to opening google and other forums to search for previously asked questions there. I could explain my error scenario to AI in my use case, to get personalized suggestions, instead of generalized suggestions which don't always fit individual issues.
**One realization:** AI can support learning more effectively and efficiently, only if it is used the correct way. After using AI in this project, I found out some plus-points of the correct use of AI, which gives me confidence to do more, while also learning (and not discarding information once the task is done). Therefore, I feel, if there would be some lectures/workshops teaching “Assistive AI usage”, explaining the pros and cons, it will benefit both the students and the university. 
