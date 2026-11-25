# Idea Log

**Purpose:** Record brainstorming, potential projects and design ideas separately from Scala learning progress. Newest entries first; link related notes and source files where relevant.

## IDEA-001 — Personal knowledge system — shared application idea

**Date:** 25 November 2026  
**Status:** Brainstorming only — not implemented.

Extend the existing [AI context management proposal](ai-context-management-proposal.md) into a reusable personal knowledge workflow for three domains: development learning, daily news and ideas.

- ChatGPT remains the discussion and research interface; do not rebuild its chat or memory features.
- GitHub stores selected knowledge in version-controlled, topic-based files. Keep development, news and ideas logically separate.
- A lightweight local Scala/Play backend and React frontend can browse, search and link to the underlying notes and source files. Package the application for normal use without keeping sbt running; no external hosting is planned.
- Start with Markdown and a consistent index, stable topic identifiers, dates, relative file links, decisions and next steps. Preserve detailed history separately from concise current context.
- For news, archive daily briefings and source links; a discussion button copies a prepared prompt for use in ChatGPT. Selected follow-up analysis can be saved back to the archive on request.
- For ideas, save selected brainstorming and revisit it later using the relevant topic files and Git history. Link to related notes and original conversations where available; do not assume full chat transcripts are automatically accessible.
- For dev-log, use the same browsing and retrieval approach to locate learning topics, examples and relevant source code without loading the entire history.
- Keep repository notes and commit messages neutral. Use pull requests for code and structural changes; consider narrowly scoped direct writes for archive data only after permissions and automation are verified.

**Next steps:** Agree on a minimal shared note/index format, then prototype local reading and navigation with existing dev-log notes. Verify scheduled GitHub writes separately before automating news delivery. No API-based chat integration is required.
