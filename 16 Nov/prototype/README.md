# RecruitPlace — UI Prototype

Static HTML/CSS/JS prototype of the **Recruitment & Placement Management System**
for the Placement Cell. It shows the intended screens, layout and demo data only.

> **UI Prototype — not connected to the Java backend.** Nothing here talks to a
> server, a database, or any API. There is no login, no saving, and no real data.

## How to open

1. Open `index.html` in any modern browser (double-click it, or serve the folder
   with any static server, e.g. `python3 -m http.server` from this folder and
   visit `http://localhost:8000/`).
2. Use the sidebar to move between pages. The **Sign In** page (`login.html`)
   is a demo only — it goes straight to the dashboard.

Demo credentials shown on the login page: `admin` / `admin123`
(prototype only — no real login).

## Pages

| Page | Contents |
| --- | --- |
| `index.html` | Dashboard — 4 stat cards, recruitment funnel, Recent Placements, Open Jobs, Applications by Status |
| `login.html` | Centred sign-in card (role select, username, password, demo note) |
| `candidates.html` | Candidate table (12 shown of 30), technology filter chips, Add Candidate demo form |
| `companies.html` | 8 company cards + company table |
| `jobs.html` | All 12 jobs with package, eligibility rules, deadline and status |
| `applications.html` | 15 applications with eligibility (ELIGIBLE / NOT_ELIGIBLE + reason) and status |
| `interviews.html` | Interview rounds table + feedback summary (ratings) |
| `offers.html` | 9 offers with package, offer/joining dates and status |
| `placements.html` | 6 placements table + 5 placed highlight cards |
| `reports.html` | 6 reports: Candidates by Technology, Open Jobs, Applications by Status, Company-wise Placements, Placed, Unplaced |

## Files

- `css/style.css` — single shared stylesheet (no Bootstrap, no framework)
- `js/app.js` — sidebar/mobile-nav toggle, active-nav highlighting, client-side
  table filter (`input[data-filter]`), filter chips, and a demo toast on form
  submit. No backend calls, no `fetch`.

## Design

- **Palette:** Deep Ink Navy `#0E2233` (primary / sidebar), Teal `#0FA3A3`
  (accent / links / active), Warm Amber `#E8A317` (highlights / CTA buttons),
  warm off-white background `#F7F5F0`, white cards, text `#1B2A3A`,
  muted `#5B6B7A`, borders `#E3DED2`.
- **Fonts:** Headings **Sora**, body **Inter** (Google Fonts, with system fallbacks).
- **Status pills:** teal/green = OPEN / ELIGIBLE / PLACED / JOINED / ACCEPTED;
  amber = APPLIED / PENDING / SCHEDULED; red = REJECTED / NOT_ELIGIBLE / CLOSED;
  navy = SHORTLISTED / INTERVIEW.
- **Responsive:** below 720 px the sidebar becomes a hamburger drawer and
  tables scroll horizontally.

## Demo data

All names, companies, jobs and figures are fixed sample data, kept consistent
across pages (same candidate codes, job codes and companies as the SQL sample
data): 8 companies (TCS, Infosys, Wipro, Accenture, Capgemini, Cognizant,
Tech Mahindra, HCLTech), 12 jobs, 30 candidates, 45 applications, 9 offers,
6 placements.
