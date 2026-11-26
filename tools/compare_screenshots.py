#!/usr/bin/env python3
"""Builds docs/screenshots/index.html: MS2 screenshots next to the MS3 Android and iOS ones.

Usage (from the repository root, after running the shared tests on Android and iOS):
    python3 tools/compare_screenshots.py

MS2 images: shared/src/commonTest/resources/ms2/screenshots (recorded by Mahjong Scoring 2 tests).
MS3 images: shared/build/screenshots/{android,ios}, copied to docs/screenshots/{android,ios}.
"""
import html
import os
import shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MS2 = os.path.join(ROOT, "shared/src/commonTest/resources/ms2/screenshots")
OUT = os.path.join(ROOT, "docs/screenshots")

columns = [("MS2 (Android, Views)", "ms2", MS2)]
for platform in ("android", "ios"):
    src = os.path.join(ROOT, "shared/build/screenshots", platform)
    dst = os.path.join(OUT, platform)
    if os.path.isdir(src):
        shutil.rmtree(dst, ignore_errors=True)
        shutil.copytree(src, dst)
    columns.append((f"MS3 {'Android' if platform == 'android' else 'iOS'}", platform, dst))

os.makedirs(OUT, exist_ok=True)
ms2_dst = os.path.join(OUT, "ms2")
shutil.rmtree(ms2_dst, ignore_errors=True)
shutil.copytree(MS2, ms2_dst)

names = sorted(f[:-4] for f in os.listdir(MS2) if f.endswith(".png"))
rows = []
for name in names:
    cells = []
    for _, folder, path in columns:
        images = [f"{name}.png", f"{name}_dialog.png"] if folder != "ms2" else [f"{name}.png"]
        found = [i for i in images if os.path.exists(os.path.join(path, i))]
        cells.append("".join(f'<img loading="lazy" src="{folder}/{i}">' for i in found) or '<span class="missing">missing</span>')
    rows.append(f"<tr><th>{html.escape(name)}</th>" + "".join(f"<td>{c}</td>" for c in cells) + "</tr>")

with open(os.path.join(OUT, "index.html"), "w") as f:
    f.write("""<!doctype html><meta charset="utf-8"><title>MS2 vs MS3 screenshots</title>
<style>body{font-family:sans-serif;margin:16px}table{border-collapse:collapse}th,td{border:1px solid #ccc;padding:6px;vertical-align:top}
img{width:260px;display:block;margin-bottom:4px}.missing{color:#c00}th{position:sticky;left:0;background:#fff}</style>
<h1>Mahjong Scoring 2 vs 3</h1><table><tr><th></th>""" + "".join(f"<th>{c[0]}</th>" for c in columns) + "</tr>" + "".join(rows) + "</table>")
print(f"{len(names)} screens -> {os.path.join(OUT, 'index.html')}")
