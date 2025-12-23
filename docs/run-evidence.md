# LunaHealer - TestNG Self-Healing Evidence

Screenshots from real Selenium + TestNG executions demonstrating LunaHealer’s layered
locator recovery strategy:

**Primary → ALT → SmartFallback → AI Resolver**

---

## Login Flow - AI Resolver Healing

<p align="center">
  <img src="https://i.postimg.cc/y6GfpwgD/Luna-Healer-Test-NG-Run.png" width="900" />
</p>

Primary locator, alternatives, and semantic fallback all failed.
LunaHealer escalated to the AI resolver, which correctly identified
`button[type='submit']` and healed the test.

---

## Dashboard & Transfer - ALT + SmartFallback

<p align="center">
  <img src="https://i.postimg.cc/9MnBS7fS/Luna-Healer-Test-NG-Run-2.png" width="900" />
</p>

Broken primary locators recovered through valid CSS alternatives and
semantic token matching without invoking AI.

---

## Profile & Contact Settings - SmartFallback

<p align="center">
  <img src="https://i.postimg.cc/sg4qLTRR/Luna-Healer-Test-NG-Run-3.png" width="900" />
</p>

Healing based on semantic intent and button text when IDs and selectors
were intentionally broken.

---

## End-to-End Completion & Report Generation

<p align="center">
  <img src="https://i.postimg.cc/D0GYPgcQ/Luna-Healer-Test-NG-Run-4.png" width="900" />
</p>

Full E2E scenario completed successfully.

---

**All tests passed.**  
Evidence captured from real TestNG executions.
