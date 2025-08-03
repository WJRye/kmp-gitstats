# GitStats Desktop

**GitStats Desktop** is a cross-platform Git repository analyzer and visualizer, built with Kotlin Multiplatform (KMP) and Compose Multiplatform (CMP). It helps you understand your codebase and development activities with rich visual insights.

---

## Why GitStats?

There are existing tools for Git statistics ([py gitstats](https://pypi.org/project/gitstats/)), but many of them are:

- Built with Python stacks and lack modern UI/UX
- Not actively maintained

**GitStats Desktop** addresses these problems by:

- Using modern tech: Kotlin Multiplatform & Compose for desktop UI
- Running natively on macOS, Windows
- Providing interactive and smooth visualizations for Git data

---

## Features

### Overview
- Project name and report generation time
- Analysis period and project age
- Total files and lines of code (added/removed)
- Total commits and daily commit averages
- Number of authors and average commits per author

### Activity
- Lines and commits changed over the past week
- Coding time heatmap (active hours & weekdays)
- Top changed files in recent activity

### Authors
- Top contributors by commits, lines added/removed
- Contribution activity trends over time per author

### Files
- File size changes over time
- Summary of file types and their total sizes

### Lines
- Lines changed over time (insertions vs. deletions)
- Net lines of code growth trend

### Tags
- Summary of tag information: name, date, tagger, commit SHA, and message

---

## Tech Stack

- **Kotlin Multiplatform** (JVM-based analysis)
- **Compose Multiplatform** for UI
- **Git CLI** for parsing repository data (via command-line interface)

---