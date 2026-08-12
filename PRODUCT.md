# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Exam administrators create assessments, manage questions and review submissions. Students register, take timed exams, inspect their results and track progress.

## Product Purpose

ExamFlow provides a small institution or training team with a self-hosted workflow for creating and completing online assessments. Success means an administrator can publish an exam safely and a student can complete it clearly on desktop or mobile.

## Positioning

ExamFlow keeps the complete assessment workflow in one compact Spring Boot application that is easy to run, inspect and adapt.

## Operating Context

The application is used in a browser. Administrators manage exams and students; students use a distraction-resistant timed examination view and personal results history.

## Capabilities and Constraints

- Spring Boot, Thymeleaf, Spring Security and JPA.
- H2 is the zero-configuration local database; connection settings are environment-driven for deployment.
- Profile images are stored on the local filesystem and are restricted to authenticated users.
- Roles are administrator and student.
- The interface language is English.

## Brand Commitments

The product name is ExamFlow. The voice is concise, calm and practical.

## Evidence on Hand

The repository contains working application flows but no verified customer claims, usage metrics or commercial proof. Future work must not invent them.

## Product Principles

- Make exam state and remaining time unambiguous.
- Protect assessment and account data by default.
- Keep administration efficient and auditable.
- Work well with a keyboard and on narrow screens.
- Favor a small, understandable deployment footprint.

## Accessibility & Inclusion

Use semantic HTML, visible keyboard focus, descriptive labels, sufficient contrast and reduced-motion support. Target WCAG 2.2 AA for all core workflows.
