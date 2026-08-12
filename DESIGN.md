---
name: ExamFlow
description: A calm, precise workspace for secure self-hosted assessment.
colors:
  ink: "#172033"
  muted: "#5d687a"
  paper: "#ffffff"
  canvas: "#f4f6f9"
  line: "#d9dee8"
  line-strong: "#b7c0cf"
  brand: "#2447c6"
  brand-dark: "#18358f"
  brand-soft: "#e9edff"
  amber: "#9a5a00"
  amber-soft: "#fff3d6"
  danger: "#b42335"
  danger-dark: "#871526"
  danger-soft: "#fdebed"
  success: "#176b47"
  success-soft: "#e5f5ed"
  focus-ring: "#82a2ff"
typography:
  display:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "clamp(2.25rem, 5vw, 4.8rem)"
    fontWeight: 700
    lineHeight: 1.15
    letterSpacing: "-0.025em"
  headline:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "clamp(1.7rem, 3vw, 2.5rem)"
    fontWeight: 700
    lineHeight: 1.15
    letterSpacing: "-0.025em"
  title:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "1.25rem"
    fontWeight: 700
    lineHeight: 1.15
    letterSpacing: "-0.025em"
  body:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: 1.55
  label:
    fontFamily: "ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif"
    fontSize: "1rem"
    fontWeight: 720
    lineHeight: 1.55
rounded:
  compact: "8px"
  field: "9px"
  control: "10px"
  option: "11px"
  surface: "14px"
  feature: "20px"
  pill: "999px"
spacing:
  xs: "0.5rem"
  sm: "0.75rem"
  md: "1rem"
  lg: "1.5rem"
  xl: "2rem"
  2xl: "3rem"
  3xl: "5rem"
components:
  button-primary:
    backgroundColor: "{colors.brand}"
    textColor: "{colors.paper}"
    typography: "{typography.label}"
    rounded: "{rounded.control}"
    padding: "0.65rem 1rem"
    height: "44px"
  button-primary-hover:
    backgroundColor: "{colors.brand-dark}"
    textColor: "{colors.paper}"
  button-secondary:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    typography: "{typography.label}"
    rounded: "{rounded.control}"
    padding: "0.65rem 1rem"
    height: "44px"
  button-danger:
    backgroundColor: "{colors.danger}"
    textColor: "{colors.paper}"
    typography: "{typography.label}"
    rounded: "{rounded.control}"
    padding: "0.65rem 1rem"
    height: "44px"
  field:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    typography: "{typography.body}"
    rounded: "{rounded.field}"
    padding: "0.7rem 0.8rem"
    height: "46px"
  status:
    backgroundColor: "{colors.brand-soft}"
    textColor: "{colors.brand}"
    rounded: "{rounded.pill}"
    padding: "0.2rem 0.6rem"
    height: "28px"
  panel:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.surface}"
    padding: "1.4rem"
  exam-option:
    backgroundColor: "{colors.paper}"
    textColor: "{colors.ink}"
    rounded: "{rounded.option}"
    padding: "1rem"
  exam-option-selected:
    backgroundColor: "{colors.brand-soft}"
    textColor: "{colors.ink}"
    rounded: "{rounded.option}"
    padding: "1rem"
---

# Design System: ExamFlow

## Overview

**Creative North Star: "The Quiet Proctor"**

ExamFlow feels like a quiet proctor: present, legible and disciplined without competing with the work. Deep ink frames important moments, white paper surfaces hold the task, and a decisive blue makes actions and current state unmistakable.

The interface is compact but breathable. Fine borders organize routine information; selective shadow lifts only the few surfaces that need attention. Semantic color appears as concise feedback, while the timed examination workspace removes the general application chrome and keeps the question, remaining time and navigation in view.

**Key Characteristics:**

- Cool, low-contrast paper-and-canvas layering
- Dark ink anchors with restrained blue emphasis
- Clear task hierarchy and minimal visual noise
- Rounded controls with precise borders
- Purposeful feedback for progress, success, review and danger

## Colors

The palette is a cool institutional neutral system energized by a single clear blue and tightly scoped semantic colors.

### Primary

- **Decisive Blue** (`brand`): Primary actions, links, current navigation, question numbering and selected answers.
- **Deep Action Blue** (`brand-dark`): Hover emphasis for blue actions and links.
- **Quiet Blue Wash** (`brand-soft`): Selected, informational and avatar-supporting backgrounds.
- **Focus Blue** (`focus-ring`): The global visible keyboard-focus outline.

### Neutral

- **Deep Ink** (`ink`): Primary text, hero fields, exam top bars, metric strips and footer anchors.
- **Slate Copy** (`muted`): Secondary instructions, metadata and table labels.
- **Paper White** (`paper`): Panels, cards, controls and navigation surfaces.
- **Cool Canvas** (`canvas`): The default page background and disabled field fill.
- **Fine Rule** (`line`): Low-emphasis surface divisions and table rows.
- **Firm Rule** (`line-strong`): Control and option boundaries that need clearer affordance.

### Semantic Feedback

- **Review Amber** (`amber`) on **Amber Wash** (`amber-soft`): Needs-review status.
- **Action Red** (`danger`) and **Deep Action Red** (`danger-dark`) on **Red Wash** (`danger-soft`): Destructive controls, errors and incorrect answers.
- **Assurance Green** (`success`) on **Green Wash** (`success-soft`): Completed states, positive messages and correct answers.

**The One Accent Rule.** Use blue for action and current selection; reserve amber, red and green for semantic outcomes.

**The Paper-on-Canvas Rule.** Routine work sits on white surfaces over the cool canvas, separated by borders before shadow.

## Typography

**Display Font:** System sans (`ui-sans-serif`, `system-ui`, platform fallbacks)

**Body Font:** System sans (`ui-sans-serif`, `system-ui`, platform fallbacks)

**Character:** A single native sans stack keeps the product fast, familiar and dependable. Large headings tighten their spacing for authority; body copy stays open and readable, while numerical scores and timers use tabular figures for stability.

### Hierarchy

- **Display** (700, fluid `clamp(2.25rem, 5vw, 4.8rem)`, 1.15): Landing and primary page headings.
- **Headline** (700, fluid `clamp(1.7rem, 3vw, 2.5rem)`, 1.15): Section-level hierarchy.
- **Title** (700, `1.25rem`, 1.15): Compact cards and grouped content.
- **Body** (400, `1rem`, 1.55): Instructions, form content and operational detail; introductory copy generally stays within 58-70 characters.
- **Label** (typically 680-760, `0.78rem-1rem`): Controls, form labels, metadata and uppercase table headers.

**The Sentence Case Rule.** Keep interface copy in sentence case; uppercase is reserved for compact table headers.

**The Stable Numbers Rule.** Timers, scores and metric values use tabular figures so changing values do not shift the layout.

## Layout

Routine application pages use a centered content frame (`1180px`) with `1rem` minimum side gutters. The examination workspace expands to `1320px` and pairs the question column with a sticky `280px` navigator. Dashboards, profiles and the landing page use two-column grids at wide widths; forms use two equal columns when space allows.

Spacing follows a recurring `0.5rem`, `0.75rem`, `1rem`, `1.5rem`, `2rem`, `3rem` and `5rem` rhythm. At `900px`, major two-column layouts stack and the exam navigator moves into flow. At `700px`, navigation becomes a menu, forms and metrics become single-column, the landing illustration loses its tilt, and tables become labeled record cards.

**The Bounded Workspace Rule.** Keep general work inside the `1180px` frame and grant the timed exam extra width only for its question-and-navigator split.

## Elevation & Depth

ExamFlow is bordered and tonal by default. The standard ambient shadow (`0 12px 30px rgba(25, 37, 63, 0.08)`) is limited to account popovers, authentication panels and active question cards. The larger landing illustration alone uses a presentation shadow (`0 30px 70px rgba(0, 0, 0, 0.3)`) to separate the example board from the dark hero.

### Shadow Vocabulary

- **Ambient Surface** (`0 12px 30px rgba(25, 37, 63, 0.08)`): Focused work surfaces and transient overlays.
- **Presentation Board** (`0 30px 70px rgba(0, 0, 0, 0.3)`): The landing-page workflow illustration only.

**The Selective Lift Rule.** Start with a fine border; add shadow only when a surface is transient, focused or intentionally presented.

## Shapes

The form language is gently rounded and precise. Panels, result blocks and exam surfaces share a `14px` radius; buttons use `10px`, fields and review rows use `9px`, and exam answers use `11px`. Compact navigator controls use `8px`. Status labels are fully pill-shaped, while step markers and avatars are circular. Borders remain one pixel and visible on actionable white surfaces.

**The Nested Radius Rule.** Larger containers use the `14px` surface radius; controls inside them step down to `8px-11px` so hierarchy remains visible.

## Components

### Buttons

- **Shape:** Compact rounded rectangle (`10px`) with a minimum `44px` height and `0.65rem 1rem` padding.
- **Primary:** Decisive Blue fill, Paper White text and border, with strong label weight (720).
- **Hover / Focus:** Hover deepens to Deep Action Blue; keyboard focus uses the global `3px` Focus Blue outline with a `3px` offset.
- **Secondary:** Paper White with Deep Ink text and a Firm Rule border; hover shifts to Cool Canvas and an Ink border.
- **Danger:** Action Red with white text; hover deepens to Deep Action Red.

### Chips

- **Style:** Fully rounded status capsules (`999px`) with `0.2rem 0.6rem` padding and compact bold text.
- **State:** Blue communicates general state, green communicates completion, and amber communicates review.

### Cards / Containers

- **Corner Style:** Gently curved surface corners (`14px`).
- **Background:** Paper White over Cool Canvas, with Deep Ink content.
- **Shadow Strategy:** Flat panels use borders; authentication, popover and active question surfaces use Ambient Surface shadow.
- **Border:** One-pixel Fine Rule.
- **Internal Padding:** Usually `1.4rem`; focused question cards scale from `1.25rem` to `2.25rem` with the viewport.

### Inputs / Fields

- **Style:** Paper White fill, Deep Ink text, a one-pixel Firm Rule border, a `9px` radius and a minimum `46px` height.
- **Focus:** The global `3px` Focus Blue outline stays outside the control.
- **Error / Disabled:** Invalid controls use an Action Red border plus a one-pixel red inset emphasis; disabled controls use Cool Canvas and Slate Copy.

### Navigation

The sticky white header uses a fine bottom rule, a compact blue brand mark and medium-bold Slate Copy links. Hover moves links to Deep Ink; the current route adds a two-pixel inset blue underline. At `700px`, a bordered menu control reveals a full-width white navigation sheet below the `68px` header.

### Exam Answer Option

Answer choices are full-width bordered rows with `11px` corners and `1rem` padding. Hover and checked states use Quiet Blue Wash with a Decisive Blue border; the checked state adds a second inset blue line. The native radio remains visible and inherits the blue accent.

### Question Navigator

Square number controls use an `8px` radius and a Firm Rule border. The current question becomes solid blue with white text; answered questions use Assurance Green on Green Wash. The navigator remains sticky beside the question on wide screens and returns to normal document flow below `900px`.

## Do's and Don'ts

### Do:

- **Do** use Decisive Blue for primary actions and current state.
- **Do** preserve the `44px` button and `46px` field minimum heights for dependable interaction.
- **Do** use borders and tonal contrast to organize routine panels before adding shadow.
- **Do** keep the timer, score and metric values stable with tabular figures.
- **Do** preserve visible focus and the reduced-motion fallback on every core workflow.

### Don't:

- **Don't** use semantic amber, red or green as decorative accents.
- **Don't** turn large routine surfaces blue; Deep Ink and Paper White carry the main structure.
- **Don't** add elevation to every panel or table row.
- **Don't** hide the native exam selection control or make answer state depend on color alone.
- **Don't** retain multi-column tables, forms or workspaces when the established responsive breakpoints stack them.
