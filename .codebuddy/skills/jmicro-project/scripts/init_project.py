#!/usr/bin/env python3
"""Scaffold a new JMicro (com.jiangtj.micro) Spring Boot project from bundled templates.

Works standalone: the Gradle wrapper is bundled with this skill, templates use
Maven Central coordinates and no local clone of the jmicro repository is required.

Usage:
    python scripts/init_project.py --path ../shop-api --package com.example.shop --name shop-api
    python scripts/init_project.py --path ../shop-api --features db,config,pic
    python scripts/init_project.py --path ../shop-api --latest            # resolve newest released version
    python scripts/init_project.py --path ../shop-api --local-repo        # also look in ~/.m2

Templates live in <skill>/assets/template:
    base/                 always copied
    features/<name>/      copied only when <name> is enabled

Placeholders replaced in both file names and file contents:
    __PKG__ __PKG_PATH__ __APP_CLASS__ __PROJECT_NAME__ __PORT__ __GROUP__
    __JMICRO_VERSION__ __SPRING_BOOT_VERSION__ __KOTLIN_VERSION__

Line blocks inside text files:
    // #if db          /  # #if db|config     (any-of)
    // #endif          /  # #endif

Runner-up: without Python, copy assets/template manually, replace the
placeholders above and drop the blocks of unused features.
"""

from __future__ import annotations

import argparse
import re
import shutil
import sys
import urllib.request
from pathlib import Path

SKILL_DIR = Path(__file__).resolve().parent.parent
TEMPLATE_DIR = SKILL_DIR / "assets" / "template"
WRAPPER_DIR = SKILL_DIR / "assets" / "gradle-wrapper"

FEATURES = ("db", "config", "pic")

# Versions CORS: keep in sync with the jmicro release this skill targets.
DEFAULT_JMICRO_VERSION = "0.4.0"
DEFAULT_SPRING_BOOT_VERSION = "4.1.1"
DEFAULT_KOTLIN_VERSION = "2.4.20"
MAVEN_METADATA = "https://repo1.maven.org/maven2/com/jiangtj/micro/micro-dependencies/maven-metadata.xml"

TEXT_SUFFIXES = {".kt", ".kts", ".java", ".properties", ".sql", ".xml", ".json", ".md", ".yml", ".yaml", ".toml", ""}
IF_RE = re.compile(r"^\s*(?://|#)\s*#if\s+([a-z0-9_|.\- ]+)\s*$", re.IGNORECASE)
ENDIF_RE = re.compile(r"^\s*(?://|#)\s*#endif\s*$", re.IGNORECASE)


def latest_released_version() -> str | None:
    """Query Maven Central for the newest released micro-dependencies version."""
    try:
        with urllib.request.urlopen(MAVEN_METADATA, timeout=10) as response:
            text = response.read().decode("utf-8", errors="replace")
    except Exception as error:  # network restrictions are common; fall back silently
        print(f"[warn] cannot read Maven Central metadata ({error}); using {DEFAULT_JMICRO_VERSION}")
        return None
    releases = re.findall(r"<version>([^<]+)</version>", text)
    stable = [v for v in releases if not any(tag in v.lower() for tag in ("alpha", "beta", "rc", "snapshot"))]
    if stable:
        return stable[-1]
    return releases[-1] if releases else None


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
    def __init__(self, args: argparse.Namespace, jmicro_version: str) -> None:
        self.package = args.package
        self.pkg_path = args.package.replace(".", "/")
        self.name = args.name
        self.app_class = args.app_class or pascal_case(args.name)
        self.port = str(args.port)
        self.group = args.group or default_group(args.package)
        self.jmicro = jmicro_version
        self.spring_boot = args.spring_boot_version or DEFAULT_SPRING_BOOT_VERSION
        self.kotlin = args.kotlin_version or DEFAULT_KOTLIN_VERSION
        self.features = {f.strip().lower() for f in args.features.split(",") if f.strip()}
        if args.local_repo:
            self.features.add("local-repo")

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


def copy_wrapper(dest_root: Path) -> list[str]:
    """Install the bundled Gradle wrapper (no local clone of jmicro required)."""
    copied: list[str] = []
    mapping = {
        "gradlew": "gradlew",
        "gradlew.bat": "gradlew.bat",
        "gradle-wrapper.jar": "gradle/wrapper/gradle-wrapper.jar",
        "gradle-wrapper.properties": "gradle/wrapper/gradle-wrapper.properties",
    }
    for source_name, relative_target in mapping.items():
        source = WRAPPER_DIR / source_name
        if not source.is_file():
            continue
        target = dest_root / relative_target
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)
        copied.append(relative_target)
    return copied


def main() -> int:
    parser = argparse.ArgumentParser(description="Scaffold a JMicro Spring Boot project")
    parser.add_argument("--path", required=True, help="target project directory")
    parser.add_argument("--package", default="com.example.app", help="base package")
    parser.add_argument("--name", default=None, help="project name (kebab-case)")
    parser.add_argument("--port", default=8080, type=int)
    parser.add_argument("--group", default=None, help="Maven groupId")
    parser.add_argument("--app-class", default=None, help="main class prefix, e.g. Shop")
    parser.add_argument("--features", default="", help="comma separated: db,config,pic")
    parser.add_argument("--jmicro-version", default=None, help=f"default {DEFAULT_JMICRO_VERSION}")
    parser.add_argument("--spring-boot-version", default=None, help=f"default {DEFAULT_SPRING_BOOT_VERSION}")
    parser.add_argument("--kotlin-version", default=None, help=f"default {DEFAULT_KOTLIN_VERSION}")
    parser.add_argument("--latest", action="store_true", help="resolve the newest released version from Maven Central")
    parser.add_argument("--local-repo", action="store_true", help="add mavenLocal() for unpublished builds")
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

    unknown = {f.strip().lower() for f in args.features.split(",") if f.strip()} - set(FEATURES)
    if unknown:
        print(f"[error] unknown features: {', '.join(sorted(unknown))} (allowed: {', '.join(FEATURES)})", file=sys.stderr)
        return 1

    jmicro_version = args.jmicro_version
    if not jmicro_version and args.latest:
        jmicro_version = latest_released_version()
        if jmicro_version and jmicro_version != DEFAULT_JMICRO_VERSION:
            print(f"[warn] Maven Central latest is {jmicro_version}, but this skill's templates target "
                  f"{DEFAULT_JMICRO_VERSION}. Older releases may lack APIs the templates use "
                  f"(BCryptUtils, com.jiangtj.micro.common.schema).")
    ctx = Context(args, jmicro_version or DEFAULT_JMICRO_VERSION)

    created = copy_tree(TEMPLATE_DIR / "base", target, ctx)
    for feature in sorted(ctx.features & set(FEATURES)):
        feature_dir = TEMPLATE_DIR / "features" / feature
        if feature_dir.is_dir():
            created += copy_tree(feature_dir, target, ctx)

    wrapper: list[str] = []
    if not args.no_wrapper:
        wrapper = copy_wrapper(target)

    print(f"[ok] created {len(created)} files at {target}")
    print(f"     package      : {ctx.package}")
    print(f"     app class    : {ctx.app_class}Application")
    print(f"     jmicro       : {ctx.jmicro} ({'mavenLocal + Maven Central' if args.local_repo else 'Maven Central'})")
    print(f"     spring boot  : {ctx.spring_boot} / kotlin {ctx.kotlin} / java 21")
    print(f"     features     : {', '.join(sorted(ctx.features & set(FEATURES))) or '(none)'}")
    print(f"     gradle wrapper: {', '.join(wrapper) if wrapper else 'NOT installed'}")
    print("[next] ./gradlew build   then   ./gradlew bootRun")
    print("       login: POST /login {\"username\":\"admin\",\"password\":\"admin123\"}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
