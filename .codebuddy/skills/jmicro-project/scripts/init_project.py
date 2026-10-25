#!/usr/bin/env python3
"""Scaffold a new JMicro (com.jiangtj.micro) Spring Boot project from bundled templates.

Usage:
    python scripts/init_project.py --path ../my-app --package com.example.app --name my-app --port 8080
    python scripts/init_project.py --path ../my-app --features db,config,pic

Templates live in <skill>/assets/template:
    base/                 always copied
    features/<name>/      copied only when <name> is enabled

Placeholders replaced in both file names and file contents:
    __PKG__ __PKG_PATH__ __APP_CLASS__ __PROJECT_NAME__ __PORT__ __GROUP__
    __JMICRO_VERSION__ __SPRING_BOOT_VERSION__ __KOTLIN_VERSION__

Line blocks inside text files:
    // #if db          /  # #if db|config     (any-of)
    // #endif          /  # #endif
"""

from __future__ import annotations

import argparse
import re
import shutil
import sys
from pathlib import Path

SKILL_DIR = Path(__file__).resolve().parent.parent
TEMPLATE_DIR = SKILL_DIR / "assets" / "template"
OWNER_REPO = SKILL_DIR.parents[2]

FEATURES = ("db", "config", "pic")
TEXT_SUFFIXES = {".kt", ".kts", ".java", ".properties", ".sql", ".xml", ".json", ".md", ".yml", ".yaml", ".toml", ""}
IF_RE = re.compile(r"^\s*(?://|#)\s*#if\s+([a-z0-9_|.\- ]+)\s*$", re.IGNORECASE)
ENDIF_RE = re.compile(r"^\s*(?://|#)\s*#endif\s*$", re.IGNORECASE)


def parse_properties(path: Path) -> dict[str, str]:
    result: dict[str, str] = {}
    if not path.is_file():
        return result
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        result[key.strip()] = value.strip()
    return result


def read_jmicro_versions(repo: Path) -> dict[str, str]:
    """Read versions from the jmicro repository (single source of truth)."""
    versions: dict[str, str] = {}
    props = parse_properties(repo / "gradle.properties")
    if props.get("version"):
        versions["jmicro"] = props["version"]

    toml = repo / "gradle" / "libs.versions.toml"
    if toml.is_file():
        text = toml.read_text(encoding="utf-8")
        for key, name in (
            ("springBoot", "springBoot"),
            ("kotlin", "kotlin"),
            ("springDependencyManagement", "depManagement"),
            ("java", "java"),
        ):
            match = re.search(rf'^{key}\s*=\s*"([^"]+)"', text, re.MULTILINE)
            if match:
                versions[name] = match.group(1)
    return versions


def pascal_case(name: str) -> str:
    parts = re.split(r"[^0-9a-zA-Z]+", name)
    return "".join(p[:1].upper() + p[1:] for p in parts if p) or "App"


def default_group(package: str) -> str:
    parts = package.split(".")
    return ".".join(parts[:-1]) if len(parts) > 2 else package


def filter_if_blocks(text: str, enabled: set[str]) -> str:
    out: list[str] = []
    skip_depth = 0
    for line in text.splitlines(keepends=True):
        if_match = IF_RE.match(line)
        if if_match and not skip_depth:
            needed = {item.strip() for item in if_match.group(1).split("|") if item.strip()}
            if needed & enabled:
                continue
            skip_depth = 1
            continue
        if ENDIF_RE.match(line):
            skip_depth = 0
            continue
        if not skip_depth:
            out.append(line)
    return "".join(out)


class Context:
    def __init__(self, args: argparse.Namespace, versions: dict[str, str]) -> None:
        self.package = args.package
        self.pkg_path = args.package.replace(".", "/")
        self.name = args.name
        self.app_class = args.app_class or pascal_case(args.name)
        self.port = str(args.port)
        self.group = args.group or default_group(args.package)
        self.jmicro = args.jmicro_version or versions.get("jmicro", "0.3.3")
        self.spring_boot = args.spring_boot_version or versions.get("springBoot", "4.1.1")
        self.kotlin = args.kotlin_version or versions.get("kotlin", "2.4.20")
        self.features = set(args.features)

    def apply(self, text: str) -> str:
        replacements = {
            "__PKG_PATH__": self.pkg_path,
            "__PKG__": self.package,
            "__APP_CLASS__": self.app_class,
            "__PROJECT_NAME__": self.name,
            "__PORT__": self.port,
            "__GROUP__": self.group,
            "__JMICRO_VERSION__": self.jmicro,
            "__SPRING_BOOT_VERSION__": self.spring_boot,
            "__KOTLIN_VERSION__": self.kotlin,
        }
        for key, value in replacements.items():
            text = text.replace(key, value)
        return text


def copy_tree(src: Path, dest_root: Path, ctx: Context) -> list[Path]:
    created: list[Path] = []
    for item in sorted(src.rglob("*")):
        rel = item.relative_to(src).as_posix()
        rel = rel.replace("__PKG_PATH__", ctx.pkg_path).replace("__APP_CLASS__", ctx.app_class)
        target = dest_root / Path(rel)
        if item.is_dir():
            target.mkdir(parents=True, exist_ok=True)
            continue
        target.parent.mkdir(parents=True, exist_ok=True)
        if item.suffix.lower() in TEXT_SUFFIXES:
            text = item.read_text(encoding="utf-8")
            text = filter_if_blocks(text, ctx.features)
            target.write_text(ctx.apply(text), encoding="utf-8", newline="\n")
        else:
            shutil.copy2(item, target)
        created.append(target)
    return created


def copy_wrapper(repo: Path, dest_root: Path) -> list[str]:
    copied: list[str] = []
    candidates = ["gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar", "gradle/wrapper/gradle-wrapper.properties"]
    for name in candidates:
        src = repo / name
        if not src.is_file():
            continue
        target = dest_root / name
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, target)
        copied.append(name)
    return copied


def install_hint(version: str) -> str:
    m2 = Path.home() / ".m2" / "repository" / "com" / "jiangtj" / "micro" / "micro-auth" / version
    return "" if m2.is_dir() else version


def main() -> int:
    parser = argparse.ArgumentParser(description="Scaffold a JMicro Spring Boot project")
    parser.add_argument("--path", required=True, help="target project directory")
    parser.add_argument("--package", default="com.example.app", help="base package")
    parser.add_argument("--name", default=None, help="project name (kebab-case)")
    parser.add_argument("--port", default=8080, type=int)
    parser.add_argument("--group", default=None, help="Maven groupId")
    parser.add_argument("--app-class", default=None, help="main class prefix, e.g. Shop")
    parser.add_argument("--features", default="", help="comma separated: db,config,pic")
    parser.add_argument("--jmicro-repo", default=None, help="jmicro repository root (version discovery + gradle wrapper)")
    parser.add_argument("--jmicro-version", default=None)
    parser.add_argument("--spring-boot-version", default=None)
    parser.add_argument("--kotlin-version", default=None)
    parser.add_argument("--no-wrapper", action="store_true")
    parser.add_argument("--force", action="store_true", help="allow non-empty target directory")
    args = parser.parse_args()

    if not TEMPLATE_DIR.is_dir():
        print(f"[error] template directory not found: {TEMPLATE_DIR}", file=sys.stderr)
        return 1

    target = Path(args.path).expanduser().resolve()
    if target.exists() and any(target.iterdir()) and not args.force:
        print(f"[error] target directory is not empty: {target} (use --force)", file=sys.stderr)
        return 1

    if not args.name:
        args.name = target.name.lower().replace("_", "-").replace(" ", "-") or "app"

    features = {f.strip().lower() for f in args.features.split(",") if f.strip()}
    unknown = features - set(FEATURES)
    if unknown:
        print(f"[error] unknown features: {', '.join(sorted(unknown))} (allowed: {', '.join(FEATURES)})", file=sys.stderr)
        return 1
    args.features = features

    repo = Path(args.jmicro_repo).expanduser().resolve() if args.jmicro_repo else OWNER_REPO
    versions = read_jmicro_versions(repo) if repo.is_dir() else {}

    ctx = Context(args, versions)
    created = copy_tree(TEMPLATE_DIR / "base", target, ctx)
    for feature in sorted(features):
        feature_dir = TEMPLATE_DIR / "features" / feature
        if feature_dir.is_dir():
            created += copy_tree(feature_dir, target, ctx)

    wrapper: list[str] = []
    if not args.no_wrapper:
        wrapper = copy_wrapper(repo, target)

    print(f"[ok] created {len(created)} files at {target}")
    print(f"     package      : {ctx.package}")
    print(f"     app class    : {ctx.app_class}Application")
    print(f"     jmicro       : {ctx.jmicro}")
    print(f"     spring boot  : {ctx.spring_boot} / kotlin {ctx.kotlin}")
    print(f"     features     : {', '.join(sorted(features)) or '(none)'}")
    print(f"     gradle wrapper: {', '.join(wrapper) if wrapper else 'NOT copied (run `gradle wrapper` manually)'}")

    missing = install_hint(ctx.jmicro)
    if missing:
        print(f"[warn] com.jiangtj.micro:micro-*:{missing} not found in ~/.m2; run "
              f"`./gradlew publishToMavenLocal -x test -PsignAllPublications=false "
              f"-PmavenCentralPublishing=false` in {repo} before building.")
    print("[next] ./gradlew build  ->  ./gradlew bootRun")
    return 0


if __name__ == "__main__":
    sys.exit(main())
