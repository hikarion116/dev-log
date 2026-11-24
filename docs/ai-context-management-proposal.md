# AI Context Management — GitHub Workflow Proposal

**Project:** `dev-log`  
**Created:** 24 November 2026  
**Version:** 0.1  
**Status:** Draft for discussion — not approved or implemented  
**Suggested location:** `docs/ai-context-management-proposal.md`

## 1. Background and purpose

The Dev Log is both a Scala learning application and an experiment in long-term human–AI pair programming. We want to understand how a developer and AI can learn, build and maintain context together without the developer becoming dependent on AI.

Our existing `docs/dev-log-progress.md` records learning objectives, exercises, code examples, design decisions, official documentation and future learning directions. At approximately learning Day 10, the file already contains roughly 775 lines. It remains manageable, but it will continue growing.

The concern is not simply whether an LLM can fit a large file into its context window. It is whether it can reliably distinguish current state from historical examples, recall earlier decisions, identify what has been verified and recommend a suitable next exercise.

GitHub could become our persistent, version-controlled project memory, rather than relying entirely on conversation history or repeatedly uploading the complete progress document.

> The intelligence can change, but the project memory remains ours.

## 2. Proposed principles

- GitHub should be the source of truth for committed code, project instructions, learning records and agreed design decisions. Uncommitted local work must be supplied separately when relevant.
- Separate the context AI needs for everyday work from the detailed history I want to keep for learning.
- Preserve historical notes and official documentation rather than deleting them to save context.
- Let AI take increasing responsibility for organising its own working context, subject to human review.
- Keep learning, implementation and important design decisions human-led. AI should guide, review and challenge rather than unnecessarily solve new exercises for me.
- Build the workflow first; add autonomy gradually, based on evidence.
- Keep the approach portable across ChatGPT, Codex and future local models where practical.

## 3. Candidate repository structure

The following is a proposal, not an instruction to create these files yet.

```text
dev-log/
├── AGENTS.md
├── README.md
├── build.sbt
├── docs/
│   ├── ai-context-management-proposal.md
│   ├── project-context.md
│   ├── dev-log-progress.md
│   ├── learning-index.md
│   └── learning-history/
│       ├── 001-010-scala-foundations.md
│       └── ...
└── src/
```

| File | Proposed responsibility | Initial size guideline |
| --- | --- | --- |
| `AGENTS.md` | Entry point for AI instructions, reading order, editing boundaries and links to other context files | Keep concise |
| `project-context.md` | Stable learning objectives, working principles and important enduring decisions | 1–2k tokens |
| `dev-log-progress.md` | Current implementation, recent learning, verification status, unresolved questions and next steps | 3–5k tokens |
| `learning-index.md` | Searchable map of past exercises and topics, with links to historical notes | 1–3k tokens initially |
| `learning-history/` | Detailed learning notes, historical code examples, documentation and reasoning | 5–10k tokens per file as a starting point |

These sizes are provisional working budgets, not model limits. File names, archive boundaries and whether some files should be combined remain open for discussion. Retaining the existing `dev-log-progress.md` path is proposed to reduce unnecessary disruption.

The source code remains authoritative for the committed implementation; historical code snippets should be labelled as historical. The progress file should explicitly distinguish reviewed code, locally verified execution, commits and pushes.

## 4. Proposed AI instructions

Once agreed, `AGENTS.md` should guide compatible agents to:

- Read the stable instructions and current progress before suggesting work; inspect relevant current source code and retrieve historical notes as needed.
- Apply our four recurring design questions: case class vs class, object vs class, trait vs inheritance/composition, and collections.
- Organise new learning into **Must know / Good to know / Revisit later**, with relevant official documentation and a short **What to look for** note beside each exercise.
- Let me attempt genuinely new concepts; provide complete examples for repetitive cases when that better supports learning.
- Distinguish facts, design decisions, proposed changes and unverified assumptions. Never claim tests passed, a commit exists or a push succeeded without evidence.
- Prefer explicit modelling of expected failures as values when appropriate; discuss exceptions where justified rather than imposing a blanket rule.
- Keep active context compact, update the index when archiving notes, and preserve the history of important decisions.
- Respect the approved permissions and scope of the current task.

An instructions file alone does not grant access, guarantee compliance or enforce security. Each AI tool must be checked for its actual reading and writing capabilities.

## 5. GitHub access and responsibilities

Start with restricted access to the Dev Log repository, potentially using the separate GitHub account we previously discussed. Confirm the effective repository permissions and avoid granting unnecessary access to other repositories, personal files or credentials.

Initially, AI should read and review the repository. Once the workflow is understood, a suitable write-capable agent could propose documentation-only changes on a branch or through a pull request. I review the diff and decide whether to merge. No direct changes to the default branch, force pushes, code changes outside the agreed scope or local terminal access are implied by this proposal.

| Responsibility | Human | AI |
| --- | --- | --- |
| Learning, implementation and design decisions | Lead | Teach, challenge, review |
| Running and verifying code | Verify results | Assist within approved access |
| Maintaining notes and the learning index | Review | Propose and organise updates |
| Managing context size and retrieval | Set boundaries | Identify growth, stale information and gaps |
| Approving repository changes | Approve and merge | Propose scoped changes |

Connecting GitHub does not automatically provide write access, persistent agent state or background maintenance. Until appropriate tooling is available and authorised, AI can prepare Markdown changes for me to commit manually.

## 6. Experiment and refinement plan

### Stage A — Review the proposal (now)

Save this document separately from the ongoing learning notes. In a later conversation, ask AI to read it again alongside the latest progress file and repository state. Refine the responsibilities, file layout, permissions and success criteria together. Do not create the proposed structure until we agree on it.

### Stage B — Establish a baseline and trial the structure

Around learning Day 15–20, or sooner if context problems appear, test how well a fresh AI conversation can resume the project using the existing notes. After approval, create the smallest useful version of the proposed structure, migrating history without losing details. Start with read-only access; trial reviewed documentation changes separately when possible.

### Stage C — Evaluate and refine again

Repeat the same context-recovery exercise using the new structure. Ask AI, without relying on the previous conversation, to identify:

1. The learning objectives and our four Scala design foundations.
2. The current implementation and most recent exercise.
3. Which execution results and Git milestones are actually verified.
4. A previous design decision (for example, why `require` was placed in the case-class body rather than only in the companion factory).
5. The next suitable learning step and any unfinished questions.

Compare the answers against the actual repository and progress notes. Record omissions, contradictions, outdated examples mistaken for current code, unsupported claims, approximate context consumed if measurable, and manual effort required to restore context. A smaller context is useful only if important information is still recovered accurately.

Try again around Day 30 or after roughly ten more exercises. Adjust the structure, budgets, reading order and maintenance rules according to the observed results. Archive by topic or milestone if that works better than fixed day ranges. There is no guaranteed day on which an LLM begins losing track.

## 7. Questions to resolve before implementation

- Is `AGENTS.md` plus a separate project-context file necessary, or can we start with fewer files?
- How should we index historical notes so AI can find the right topic without reading the entire history?
- Which tools can read the repository reliably, and which can propose changes with restricted permissions?
- How much documentation maintenance should we delegate, and when should AI propose archiving or correcting notes?
- What level of context-recovery accuracy and maintenance effort would justify keeping the new workflow?

## 8. Decision and revision history

| Date | Version | Decision |
| --- | --- | --- |
| 24 Nov 2026 | 0.1 | Capture the GitHub-based AI context-management idea for later review. No structure or access changes approved yet. |

**Suggested initial Git commit:**

```text
docs: propose GitHub-based AI context management workflow
```

Keep this proposal in Git so we can retrieve the original idea, compare later revisions and understand why the workflow changed. Record the actual experiment results and decisions when they happen, rather than treating this proposal as an implementation record.
