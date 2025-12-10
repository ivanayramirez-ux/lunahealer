
</head>
<body>
  <h1>LunaHealer</h1>
  <p>
    A self-healing locator engine for UI automation with AI assistance, fallback strategies,
    and full end-to-end validation.
  </p>

  <h2>Overview</h2>
  <p>
    LunaHealer is a self-healing automation framework designed to solve one of the most common
    causes of UI test failures: unstable or frequently changing DOM locators. The project
    combines traditional locator strategies, intelligent fallback logic, and an AI-assisted
    resolver to automatically recover from broken selectors during test execution.
  </p>
  <p>
    The framework is built in Java with Selenium WebDriver and TestNG, and it is demonstrated
    using a complete end-to-end flow against a custom demo application, LunaBank. The purpose
    of this project is to showcase advanced automation architecture, resiliency techniques,
    and the ability to design a production-ready healing pipeline that reduces test flakiness
    and maintenance overhead.
  </p>

  <h2>Key Features</h2>

  <h3>1. Multi-layered Healing Pipeline</h3>
  <p>LunaHealer uses a deterministic sequence of healing strategies:</p>
  <ul>
    <li>Primary locator</li>
    <li>Alternative locator list</li>
    <li>Semantic SmartFallback (intent-based XPath generator)</li>
    <li>AI Resolver (mocked API client that simulates an AI returning a corrected locator)</li>
  </ul>
  <p>
    Each time a locator fails, LunaHealer escalates through these layers until a match is found.
    Every healing decision is logged and written to a JSON report.
  </p>

  <h3>2. Locator Profiles and Semantic Hints</h3>
  <p>Each element is defined in a JSON configuration file with:</p>
  <ul>
    <li>Primary locator type and value</li>
    <li>Optional alternative locator set</li>
    <li>Semantic intent hint used by the fallback engine</li>
    <li>A human-readable description</li>
  </ul>
  <p>
    This gives the engine enough metadata to recover intelligently rather than relying on
    brittle selectors.
  </p>

  <h3>3. AI-Assisted Locator Recovery</h3>
  <p>
    The AIResolver acts as the final healing layer. When primary, alt, and semantic fallback
    strategies fail, the resolver asks a mocked AI client for a suggested locator based on
    the element’s intent.
  </p>
  <p>
    The engine then validates the AI suggestion and uses it only if it returns exactly one
    match. This layer demonstrates how a real automation pipeline could incorporate an
    LLM-powered locator repair service.
  </p>

  <h3>4. End-to-End Scenario Demonstrating All Healing Layers</h3>
  <p>The full flow covers:</p>
  <ul>
    <li>Navigating from the index page to login</li>
    <li>Performing login with intentionally broken locators to force AI healing</li>
    <li>Navigating the dashboard</li>
    <li>Executing a transfer with broken locators triggering ALT healing</li>
    <li>Returning to dashboard</li>
    <li>Navigating to Profile and updating settings using SmartFallback</li>
    <li>Navigating to Security and adjusting MFA options</li>
    <li>Interacting with the RockyInvest modal and external tab using SmartFallback and ALT healing</li>
    <li>Verifying all updates and UI messages</li>
    <li>Confirming a complete healing report JSON is generated</li>
  </ul>
  <p>
    This scenario intentionally breaks locators at multiple points to demonstrate that all four
    healing layers function correctly end-to-end.
  </p>

  <h3>5. Healing Event Reporting</h3>
  <p>Every locator lookup produces a structured event including:</p>
  <ul>
    <li>Key name</li>
    <li>Strategy used (Primary, ALT, Fallback, AI)</li>
    <li>Locator chosen</li>
    <li>Success or failure</li>
  </ul>
  <p>
    These events are aggregated into <code>lunahealer-healing-report.json</code> under the
    <code>target/</code> directory.
  </p>
  <p>
    This artifact gives full observability into healing performance and can be integrated with
    CI pipelines for debugging, analytics, or enforcing minimum healing coverage.
  </p>

 
  <h2>Technology Stack</h2>
  <ul>
    <li>Java (JDK 17)</li>
    <li>Selenium WebDriver 4</li>
    <li>TestNG</li>
    <li>Maven</li>
    <li>Mocked AI client returning structured locator suggestions</li>
    <li>Custom JSON-driven locator configuration</li>
  </ul>

  <h2>What This Project Demonstrates</h2>
  <p>
    This project shows the ability to design and implement a realistic, production-oriented
    self-healing automation framework with:
  </p>
  <ul>
    <li>Modular, extensible architecture</li>
    <li>AI-assisted decision-making</li>
    <li>Smart fallback logic driven by semantic analysis</li>
    <li>End-to-end reliability under intentionally unstable UI conditions</li>
    <li>Full observability into locator recovery behavior</li>
  </ul>
  <p>
    It reflects an understanding of modern automation challenges and how AI and structured
    metadata can be applied to significantly reduce test flakiness.
  </p>
</body>
</html>
