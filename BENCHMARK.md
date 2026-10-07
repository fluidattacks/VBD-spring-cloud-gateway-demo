# Synthetic vulnerability benchmark — NOT FOR PRODUCTION

This repository (`fluidattacks/VBD-spring-cloud-gateway-demo`, "Vulnerable By
Design") is a fork of
[ZakEnn/spring-cloud-gateway-demo](https://github.com/ZakEnn/spring-cloud-gateway-demo)
used **exclusively as a labeled test benchmark** for vulnerability-detection
tooling (SAST engines, LLM security agents).

- Commits on `benchmark/*` branches **deliberately introduce security
  weaknesses** on top of the clean upstream code. Each injected sample is one
  commit; its parent commit is the clean twin.
- Every injected weakness is recorded, with its exploitation path, in
  `GROUND_TRUTH.csv`. Candidates that were evaluated and rejected as not
  genuine are listed in `GROUND_TRUTH_rejected.csv`.
- The weakness lives in the code body without announcing itself, so a detector
  cannot cheat off a comment; the labeling lives here, in the commit messages
  and in the manifest.

**Do not deploy this code. Do not submit it upstream. Do not use it as a
dependency.** It exists only to measure whether detectors find known, labeled
weaknesses.

Weakness taxonomy: Fluid Attacks criteria (e.g. F115 — Security controls bypass
or absence).
