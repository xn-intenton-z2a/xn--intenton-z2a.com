# Plan: Repository Showcase (agentic-lib #1924)

**Created**: 2026-03-21
**Status**: PENDING
**Issue**: https://github.com/xn-intenton-z2a/agentic-lib/issues/1924
**Branch**: `claude/showcase`

---

## User Assertions

Replace the top-right "MISSION.md" link with a multi-repository showcase selector. Each repository shows its mission name (linked to MISSION.md), status, transform count, and a "make default" action. The selected default drives the VT100 terminal logs, screenshot display, and discussions widget. The default persists in browser localStorage. A "Show all" button opens a full-page 2×3 screenshot grid where clicking a repo switches the default. The 6th grid cell is a "Create your own" link to the repository0 template. Discussions switch dynamically per repo, showing the most recently updated thread.

---

## Current State

### Top-Right Mission Link
- Element: `<a id="mission-link">MISSION.md</a>` at `top: 8px; right: 8px` (fixed, z-index 2500)
- Clicking opens a lightbox showing MISSION.md content fetched from `repository0/main`
- Currently hardcoded to `xn-intenton-z2a/repository0`

### VT100 Terminal
- Polls `https://api.github.com/repos/xn-intenton-z2a/repository0/contents/?ref=agentic-lib-logs`
- Fetches last 5 `agent-log-*.md` files, concatenates, types them character-by-character
- Hardcoded to `repository0`

### Screenshot
- Loads `https://raw.githubusercontent.com/xn-intenton-z2a/repository0/agentic-lib-logs/SCREENSHOT_INDEX.png`
- Lightbox links to `repository0` GitHub Pages and repository
- Hardcoded to `repository0`

### Giscus Discussions
- Hardcoded to `repository0` with `data-mapping="specific"` and `data-term="Talk to the repository"`
- Uses `data-repo-id="R_kgDON6E8ZA"` and `data-category-id="DIC_kwDON6E8ZM4CpwzM"`

### localStorage
- None currently

---

## Repositories to Showcase

| Repository | GitHub URL |
|------------|-----------|
| `repository0` | `xn-intenton-z2a/repository0` |
| `repository0-string-utils` | `xn-intenton-z2a/repository0-string-utils` |
| `repository0-random` | `xn-intenton-z2a/repository0-random` |
| `repository0-crucible` | `xn-intenton-z2a/repository0-crucible` |
| `repository0-plot-code-lib` | `xn-intenton-z2a/repository0-plot-code-lib` |

---

## Design

### Top-Right Showcase Bar

Replace the single "MISSION.md" link with a compact list of repositories. Each entry is one line:

```
(<mission-name> → MISSION.md) [STATUS] (N transforms) (make default)
```

Or if it's the current default:

```
(<mission-name> → MISSION.md) [STATUS] (N transforms) (default ✓)
```

Below the list: a **"Show all"** button that opens the screenshot grid overlay.

**Layout**: Vertical stack of 5 entries + button, top-right corner. Same position and styling as current mission link (fixed, z-index 2500, semi-transparent, steel-blue text). Compact — one line per repo.

**Example rendering**:
```
hamming-distance  [COMPLETE]  4 transforms  (default ✓)
string-utils      [IN-PROGRESS]  12 transforms  (make default)
random            [NOT STARTED]  0 transforms  (make default)
crucible          [BLOCKED]  3 transforms  (make default)
plot-code-lib     [COMPLETE]  13 transforms  (make default)
                                    [ Show all ]
```

### Data Sources (per repository)

**Batched approach** — one Contents API call per repo for status detection:

| Data | Source | Endpoint | Calls |
|------|--------|----------|-------|
| File listing (main) | Check for MISSION.md, MISSION_COMPLETE.md, MISSION_FAILED.md, agentic-lib.toml | `GET /repos/{owner}/{repo}/contents/?ref=main` | 1 per repo |
| MISSION.md content | Full file for mission name + lightbox | `https://raw.githubusercontent.com/{owner}/{repo}/main/MISSION.md` | 1 per repo |
| Transform count | `agentic-lib-state.toml` from logs branch | `https://raw.githubusercontent.com/{owner}/{repo}/agentic-lib-logs/agentic-lib-state.toml` | 1 per repo |
| Agent logs | For VT100 (default repo only) | `GET /repos/{owner}/{repo}/contents/?ref=agentic-lib-logs` | 1 (default only) |
| Log file content | For VT100 (default repo only) | `download_url` from log listing | ~5 (default only) |
| Screenshot | For showcase + grid (lazy-loaded) | `https://raw.githubusercontent.com/{owner}/{repo}/agentic-lib-logs/SCREENSHOT_INDEX.png` | 1 per repo (lazy) |

**Total on page load**: 5 × 3 (listing + MISSION.md + state.toml) + 1 (log listing) + 5 (log files) + 1 (default screenshot) = **22 calls**. Subsequent polling: 1 log listing + 5 log files = 6 calls every 5 minutes. Screenshot grid images lazy-loaded only on "Show all" click (+4 screenshots). Well within the 60/hour unauthenticated limit.

### Status Derivation (from batched file listing)

```javascript
// Single Contents API call returns array of {name, ...} objects
const files = await fetch(`/repos/${owner}/${repo}/contents/?ref=main`).then(r => r.json());
const fileNames = Array.isArray(files) ? files.map(f => f.name) : [];

if (fileNames.includes('MISSION_COMPLETE.md')) → COMPLETE
else if (fileNames.includes('MISSION_FAILED.md')) → BLOCKED
else if (!fileNames.includes('MISSION.md')) → NOT STARTED
else → IN-PROGRESS  // has MISSION.md but no completion signal
```

This replaces 404-probing each signal file individually (saves 2 API calls per repo).

### localStorage

Key: `intention-showcase-default`
Value: repository name (e.g. `repository0`)
Default: `repository0` (if no localStorage value set)

On page load:
1. Read default from localStorage (fallback to `repository0`)
2. Fetch metadata for all 5 repos in parallel (batched — 3 calls each)
3. Render the showcase bar
4. Load VT100 logs, screenshot, and discussions for the default repo

On "make default" click (from showcase bar, grid, or anywhere):
1. Save to localStorage
2. Clear VT100 terminal, restart polling with new repo
3. Replace screenshot with new repo's screenshot
4. Switch Giscus discussions to new repo
5. Update the showcase bar (swap "make default" ↔ "default ✓")
6. Update mission lightbox content

---

## Work Items

### W1: Define repository config array
**What**: Add a JavaScript array of repository objects at the top of the script block, including Giscus IDs.
```javascript
const SHOWCASE_REPOS = [
  { name: 'repository0', label: 'repository0',
    repoId: 'R_kgDON6E8ZA', categoryId: 'DIC_kwDON6E8ZM4CpwzM' },
  { name: 'repository0-string-utils', label: 'string-utils',
    repoId: 'R_kgDORsndTg', categoryId: 'DIC_kwDORsndTs4C45WB' },
  { name: 'repository0-random', label: 'random',
    repoId: 'R_kgDORsnWVw', categoryId: 'DIC_kwDORsnWV84C45VR' },
  { name: 'repository0-crucible', label: 'crucible',
    repoId: 'R_kgDORsnROA', categoryId: 'DIC_kwDORsnROM4C45Uz' },
  { name: 'repository0-plot-code-lib', label: 'plot-code-lib',
    repoId: 'R_kgDORrzlsg', categoryId: 'DIC_kwDORrzlss4C40yA' },
];
```

### W2: Add localStorage for default repo
**What**: On page load, read `intention-showcase-default` from localStorage. Default to `repository0`. All existing GitHub API calls use this variable instead of hardcoded `repository0`.

### W3: Fetch per-repo metadata (batched)
**What**: For each repo in SHOWCASE_REPOS, fetch in parallel:
- Root file listing via Contents API (`/contents/?ref=main`) — one call gets file names for status derivation (MISSION_COMPLETE.md, MISSION_FAILED.md, MISSION.md existence)
- MISSION.md raw content (for mission name + lightbox)
- `agentic-lib-state.toml` from logs branch (for cumulative transform count)

Cache all results in a `repoMetadata` Map to avoid re-fetching on "make default" clicks or "Show all" opens.

**Rate limit budget**: 5 repos × 3 fetches = 15 API calls on page load. The 5-minute polling interval only polls the default repo (log listing + log files, not metadata for all 5).

### W4: Replace mission-link with showcase bar
**What**: Replace the `<a id="mission-link">` element with a `<div id="showcase-bar">` containing one row per repository plus a "Show all" button. Style to match existing aesthetics (steel-blue, semi-transparent, fixed top-right).

**HTML structure** per row:
```html
<div class="showcase-row" data-repo="repository0">
  <a class="showcase-mission" href="#" title="View MISSION.md">hamming-distance</a>
  <span class="showcase-status complete">COMPLETE</span>
  <span class="showcase-transforms">4 transforms</span>
  <a class="showcase-default" href="#">default ✓</a>
</div>
```

**CSS**:
- Container: `position: fixed; top: 8px; right: 8px; z-index: 2500`
- Rows: `font-size: 0.9vw; line-height: 1.6; white-space: nowrap`
- Status colours: COMPLETE=#2ea043, IN-PROGRESS=#d29922, NOT STARTED=#8b949e, BLOCKED=#f85149
- Default link: underline on non-default rows, bold on default row
- Overall opacity 0.7, hover 1.0 (like current mission link)
- "Show all" button: below the rows, same font, underline style

### W5: Wire "make default" to switch VT100 + screenshot + discussions
**What**: Single `switchDefault(repoName)` function called from showcase bar, grid, or anywhere:
1. `localStorage.setItem('intention-showcase-default', repoName)`
2. Clear terminal: `terminalContainer.innerHTML = ''`, reset state, clear timeout
3. Call `pollIntentionUrl(repoName)` — restart log polling
4. Call `buildScreenshotShowcase(repoName)` — switch screenshot
5. Call `switchGiscus(repoName)` — switch discussions (W9)
6. Update showcase bar: old default gets "make default", new default gets "default ✓"
7. Fetch and cache new MISSION.md for lightbox

### W6: Parameterise existing functions
**What**: Modify existing functions to accept a repo name parameter instead of hardcoded `repository0`:

- `pollIntentionUrl(repoName)` — change API URL to use `repoName`
- `buildScreenshotShowcase(repoName)` — change image URL and lightbox links
- Mission lightbox — load from the selected repo

### W7: Mission name click opens lightbox
**What**: Clicking the mission name in any showcase row opens the mission lightbox for that specific repo (not necessarily the default). Reuse the existing lightbox mechanism.

### W8: "Show all" screenshot grid overlay
**What**: Below the showcase bar, a "Show all" button opens a full-page overlay (z-index 5000) showing a 2×3 grid of screenshot cards.

**Layout**:
```
┌─────────────────────────────────────────────┐
│  [✕ Close]                                  │
│                                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ repo0    │  │ string-  │  │ random   │  │
│  │ screenshт│  │ utils    │  │ screenshт│  │
│  │ [STATUS] │  │ [STATUS] │  │ [STATUS] │  │
│  │ mission  │  │ mission  │  │ mission  │  │
│  └──────────┘  └──────────┘  └──────────┘  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ crucible │  │ plot-code│  │ Create   │  │
│  │ screenshт│  │ -lib     │  │ your own │  │
│  │ [STATUS] │  │ [STATUS] │  │ → Use    │  │
│  │ mission  │  │ mission  │  │ template │  │
│  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────┘
```

**6th cell**: "Create your own" card linking to `https://github.com/xn-intenton-z2a/repository0` with "Use this template" CTA. On-brand for the project's purpose as a template.

Each repo cell shows:
- Screenshot image (**lazy-loaded** — only fetched when overlay opens, not on page load)
- Repository label
- Status badge (COMPLETE/IN-PROGRESS/NOT STARTED/BLOCKED)
- Mission name (truncated)
- Border highlight on the current default
- Spinner placeholder while screenshot loads

**Interaction**: Clicking any repo cell calls `switchDefault(repoName)` and closes the overlay. Clicking the 6th cell opens the template repo in a new tab.

**CSS**: Full viewport overlay (`position: fixed; inset: 0; z-index: 5000`), dark semi-transparent backdrop (`rgba(0,0,0,0.85)`), grid uses CSS Grid (`grid-template-columns: repeat(3, 1fr)`), gap 2vw, padding 5vw. Responsive — 2 columns on portrait/mobile. Close on Escape key or ✕ button.

### W9: Dynamic Giscus discussions
**What**: Switch the Giscus embed when the default repo changes. Use the most recently updated discussion thread per repo.

**Repository Giscus IDs** (all have Discussions enabled with "General" category):

| Repository | Repo ID | General Category ID |
|------------|---------|-------------------|
| `repository0` | `R_kgDON6E8ZA` | `DIC_kwDON6E8ZM4CpwzM` |
| `repository0-string-utils` | `R_kgDORsndTg` | `DIC_kwDORsndTs4C45WB` |
| `repository0-random` | `R_kgDORsnWVw` | `DIC_kwDORsnWV84C45VR` |
| `repository0-crucible` | `R_kgDORsnROA` | `DIC_kwDORsnROM4C45Uz` |
| `repository0-plot-code-lib` | `R_kgDORrzlsg` | `DIC_kwDORrzlss4C40yA` |

**Discussion term**: Use the REST Discussions endpoint (not GraphQL — GraphQL requires authentication, REST works unauthenticated for public repos):
```
GET https://api.github.com/repos/xn-intenton-z2a/{repo}/discussions?per_page=1&direction=desc
```
Extract the discussion title from the first result. Cache it per repo. Fallback to `"Talk to the repository"` if the API call fails or returns empty.

**Note**: This adds 5 REST calls on page load (one per repo, cacheable). Budget: 15 (metadata) + 5 (discussions) + 6 (VT100 logs) + 1 (screenshot) = **27 calls** on page load. Still well within 60/hour.

**Switching mechanism**: `switchGiscus(repoName)` function:
```javascript
function switchGiscus(repoName) {
  const config = SHOWCASE_REPOS.find(r => r.name === repoName);
  const term = repoMetadata.get(repoName)?.discussionTitle || 'Talk to the repository';
  const iframe = document.querySelector('iframe.giscus-frame');
  if (iframe) {
    // Reconfigure existing iframe via postMessage
    iframe.contentWindow.postMessage({
      giscus: {
        setConfig: {
          repo: 'xn-intenton-z2a/' + repoName,
          repoId: config.repoId,
          category: 'General',
          categoryId: config.categoryId,
          term: term,
        }
      }
    }, 'https://giscus.app');
  } else {
    // Iframe not loaded yet — replace the script tag
    const container = document.getElementById('giscus-container');
    container.innerHTML = '';
    const script = document.createElement('script');
    script.src = 'https://giscus.app/client.js';
    script.setAttribute('data-repo', 'xn-intenton-z2a/' + repoName);
    script.setAttribute('data-repo-id', config.repoId);
    script.setAttribute('data-category', 'General');
    script.setAttribute('data-category-id', config.categoryId);
    script.setAttribute('data-mapping', 'specific');
    script.setAttribute('data-term', term);
    // ... other existing attributes ...
    script.async = true;
    script.crossOrigin = 'anonymous';
    container.appendChild(script);
  }
}
```

### W10: Unified switchDefault function
**What**: All default-switching interactions (showcase bar "make default", grid cell click) route through the same `switchDefault(repoName)` function. This ensures VT100, screenshot, discussions, showcase bar, and localStorage all update atomically. W5 defines the function; W10 ensures W8's grid and any future interaction points all call it.

---

## Implementation Order

| # | Work Item | Complexity |
|---|-----------|------------|
| 1 | W1: Repository config array (incl Giscus IDs) | Trivial |
| 2 | W2: localStorage for default | Low |
| 3 | W6: Parameterise existing functions | Medium |
| 4 | W3: Fetch per-repo metadata (batched) | Medium |
| 5 | W4: Showcase bar HTML/CSS + "Show all" button | Medium |
| 6 | W5: switchDefault function (VT100 + screenshot + discussions) | Medium |
| 7 | W7: Per-repo mission lightbox | Low |
| 8 | W9: Dynamic Giscus discussions | Medium |
| 9 | W8: "Show all" screenshot grid overlay (lazy-loaded) | Medium |
| 10 | W10: Unified switchDefault wiring | Low |

All changes are in `public/index.html` (single file — inline JS and CSS). The file is currently ~750 lines; this will add ~300-400 lines. Still manageable for a no-build-step static site, but if it grows further, extracting JS into a `showcase.js` file would be a natural next step.

---

## Edge Cases

- **API rate limiting**: ~27 calls on page load + 6 every 5 min = ~99/hour max with continuous browsing. Close to the 60/hour limit if the user stays on the page for an hour. **Mitigation**: cache aggressively — metadata only fetched once on page load, polling only for the default repo's logs. If rate-limited (HTTP 403), show cached data and pause polling.
- **Repo doesn't exist or is private**: Show "unavailable" status, grey out the row and grid cell. Contents API returns 404 — handle gracefully.
- **No agentic-lib-logs branch**: Show "0 transforms", show placeholder image in grid instead of screenshot.
- **MISSION.md missing**: Show repo label instead of mission name.
- **Discussions REST API unavailable**: Fall back to `data-term="Talk to the repository"` (exists in all repos). If the REST discussions endpoint returns 404 (feature not enabled), skip gracefully.
- **Giscus iframe not ready on first switchDefault**: Use the script-replacement path instead of postMessage. Listen for iframe load event to know when postMessage becomes available.
- **Mobile/narrow viewport**: Showcase bar collapses to default repo only with a dropdown or just shows labels. Grid uses 2 columns instead of 3.
- **Screenshot loading latency**: Grid images are lazy-loaded only when "Show all" is clicked. Show a spinner per cell while loading. If a screenshot fails to load (no logs branch), show a styled placeholder with the repo name.

---

## Summary

10 work items, all in `public/index.html`. The core changes:
- **Showcase bar** (W1–W7): Replace hardcoded `repository0` with a parameterised multi-repo selector driven by localStorage, with batched API calls to minimise rate limit impact
- **Screenshot grid** (W8, W10): Full-page 2×3 grid overlay with lazy-loaded screenshots; clicking switches the default; 6th cell is "Create your own" template link
- **Dynamic discussions** (W9): Giscus widget switches per repo using REST API for discussion discovery (not GraphQL, which requires auth), with postMessage reconfiguration of the iframe
