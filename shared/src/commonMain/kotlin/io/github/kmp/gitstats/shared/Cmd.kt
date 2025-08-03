package io.github.kmp.gitstats.shared

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


/**
 *  Git command to retrieve a Git configuration value from the local repository's `.git/config` file.
 */
val GIT_CONFIG_LOCAL = arrayListOf("git", "config", "--get", "--local")

/**
 * Git command to retrieve a Git configuration value from any available scope (local, global, or system),
 * depending on where the key is defined.
 */
val GIT_CONFIG = arrayListOf("git", "config", "--get")

/**
 * Git command to retrieve detailed commit logs, including:
 * - Commit hash
 * - Author name and email
 * - Commit date (in ISO format)
 * - Commit message
 * - Number of lines added and deleted per file (with --numstat)
 * Note: "--no-renames" avoids treating renamed files specially (they appear as delete + add)
 */
val GIT_LOG_COMMIT = arrayListOf(
    "git",
    "log",
    "--pretty=format:--%n%H%n%an%n%ae%n%ad%n%s",
    "--date=iso",
    "--numstat",
    "--no-renames"
)

/**
 * Git command to get the current branch name.
 * Equivalent to: git rev-parse --abbrev-ref HEAD
 * This returns the short name of the currently checked-out branch (e.g., "main" or "develop").
 */
val GIT_BRANCH = arrayListOf("git", "rev-parse", "--abbrev-ref", "HEAD")
val GIT_PULL = arrayListOf("git", "pull")

/**
 * Git command to list detailed file statistics (including size) for a specific commit (SHA).
 *
 * This command uses `git ls-tree` with the following flags:
 * - `-r`: Recursively lists files in all subdirectories.
 * - `-l`: Shows file size in bytes.
 *
 * The `sha` value appended to the command specifies the commit or tree to inspect.
 *
 * Example output:
 *   100644 blob f2c1a81...  1234  path/to/file.txt
 *
 * This command is useful for analyzing the file structure and size at a specific point in the Git history.
 */
val GIT_FILE_STAT = arrayListOf("git", "ls-tree", "-r", "-l") //+ sha

/**
 * Git command to list all tracked files in the index (staging area).
 *
 * Description:
 * - Lists paths of files that are currently tracked by Git (i.e., files in the index).
 * - It does NOT include untracked files or ignored files.
 * - Useful for scripting or tools that need to operate on all source files under version control.
 *
 * Example Output:
 *   src/main/java/com/example/Main.kt
 *   README.md
 *   build.gradle.kts
 *
 */
val GIT_LS_FILES = arrayListOf("git", "ls-files")

/**
 * Git command to list all tags with detailed metadata using a custom format.
 *
 * Description:
 * - Lists all Git tags in the repository.
 * - Uses a custom `--format` string to output structured tag information in a tab-delimited format.
 * - Each field is separated by a `\t` (tab character, encoded here as `%09`).
 *
 * Format Fields:
 * - %(refname:strip=2)       → Tag name (e.g., "v1.0.0"), stripping the `refs/tags/` prefix.
 * - %(objecttype)            → Type of the tagged object, usually "commit", "tag", etc.
 * - %(object)                → SHA-1 hash of the tagged object.
 * - %(taggername)            → Name of the person who created the tag (for annotated tags).
 * - %(creatordate:iso)       → ISO-8601 formatted date when the tag was created.
 * - %(contents:subject)      → First line (subject) of the tag message or the commit message if lightweight.
 *
 * Example Output:
 *   v1.0.0    tag     a1b2c3d4    Alice    2024-12-31 10:00:00 +0800    Initial release
 *   v2.0.0    commit  e5f6g7h8    Bob      2025-06-15 09:00:00 +0800    Major update
 */
val GIT_TAG = arrayListOf(
    "git",
    "tag",
    "--format=%(refname:strip=2)%09%(objecttype)%09%(object)%09%(taggername)%09%(creatordate:iso)%09%(contents:subject)"
)

val GIT_TAG_SHA = arrayListOf("git", "rev-parse") //+ tag name

/**
 * Git command to show concise information about a specific commit.
 *
 * Example Output:
 *   Alice Zhang
 *   2025-08-01 20:15:00 +0800
 *   Fix issue with login timeout
 */
val GIT_TAG_SHA_INFO =
    arrayListOf("git", "show", "-s", "--format=%an%n%ad%n%s", "--date=iso") // + sha

/**
 *  Git command to retrieve the latest commit SHA reachable from a given reference,
 *  optionally filtered by a date.
 */
val GIT_COMMIT_SHA = arrayListOf("git", "rev-list", "-1")//+ --before, + sha
fun execute(path: String, args: List<String>): Pair<Int, String> {
    val process = ProcessBuilder(args).directory(File(path)).redirectErrorStream(true).start()
    val output = process.inputStream.bufferedReader().readText()
    val exitCode = process.waitFor()
    return Pair(exitCode, output)
}

fun processing(path: String, args: List<String>, useLine: (line: String) -> Unit) {
    val repoDir = File(path)
    val processBuilder = ProcessBuilder(
        args
    )
    if (path.isNotEmpty()) processBuilder.directory(repoDir)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    reader.useLines {
        it.forEach { line ->
            useLine.invoke(line)
        }
    }
    reader.close()
}

fun processed(path: String, args: List<String>): List<String> {
    val repoDir = File(path)
    val processBuilder = ProcessBuilder(
        args
    )
    if (path.isNotEmpty()) processBuilder.directory(repoDir)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val ret = reader.lineSequence().toList()
    reader.close()
    return ret
}

