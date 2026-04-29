---
name: noveo-safe-client-edit
description: Safely modify the Noveo Kotlin Android client in this repository with minimal regression risk. Use when Codex needs to change UI, state, networking, notifications, storage, build config, or release workflow files in this repo, and when the job should finish with validation, a git commit, and a git push.
---

# Noveo Safe Client Edit

Modify this client conservatively. Build context from the repo first, make the smallest coherent change, validate the change as far as the local environment allows, then review the diff, commit, and push.

## Workflow

1. Check repository state before editing.
2. Read only the files needed for the task and the nearest related module files.
3. Keep edits narrow and preserve existing conventions.
4. Validate with the strongest targeted check available locally.
5. Review the diff for accidental churn.
6. Commit with a focused message.
7. Push the current branch when finished.

## Safety Rules

- Start with `git status --short --branch`.
- Assume the worktree may contain user changes. Never revert or overwrite unrelated edits.
- Prefer targeted reads with `rg`, `Get-Content`, and focused file inspection over broad scans.
- Touch the smallest file set that can solve the task.
- Preserve module boundaries:
  - `app/` holds Compose UI, activities, app state orchestration, and app-specific data glue.
  - `core/network/` holds endpoint and networking support.
  - `core/datastore/` holds persistence keys and storage support.
  - `core/notifications/` holds notification channels and notification support.
  - `core/voice/` holds voice-related parity contracts.
- Treat large files such as `app/src/main/java/ir/hienob/noveo/ui/HomeUi.kt` carefully. Search for the exact feature area first, then edit only the relevant block.
- If a task implies behavior changes across UI and state, inspect both the Compose surface and `AppViewModel` before patching.

## Repo-Specific Guidance

- Read `references/project-map.md` when you need a quick map of the repo and likely edit points.
- Use the CI workflow in `.github/workflows/build.yml` as the source of truth for release validation. This repo builds with `assembleRelease`.
- There may be no Gradle wrapper checked in. If `./gradlew` is absent, prefer `gradle` for local validation when available.
- Keep Kotlin and Compose changes idiomatic to the existing codebase. Match current naming, state flow usage, and file organization before introducing new patterns.

## Validation

Choose the narrowest check that gives real confidence:

- For simple text or resource edits, inspect the exact diff and any directly referenced call sites.
- For Kotlin source edits, prefer a relevant Gradle task if the toolchain is available.
- For broader app behavior changes, prefer `gradle assembleRelease` because CI uses that path.
- If the environment cannot run Gradle or Android builds, state that explicitly and still perform static checks such as focused searches and diff review.

Do not claim the change is verified if you could not run a meaningful check.

## Git Finish

After validation:

1. Review `git diff --stat` and `git diff -- <paths>` for the touched files.
2. Stage only the intended files.
3. Commit with a focused message that matches the actual change.
4. Push the current branch.

Use this sequence when the work is complete:

```powershell
git status --short --branch
git add <intended-files>
git commit -m "<focused message>"
git push
```

If `git push` fails because the branch has no upstream, use:

```powershell
git push -u origin <current-branch>
```

If push is blocked by permissions, missing auth, or sandbox approval, report that clearly instead of pretending it succeeded.
