#!/usr/bin/env python3
"""Stage build identity only. Never patch upstream behavior or tracked files here."""
from pathlib import Path
import shutil
import re

root=Path(__file__).resolve().parents[1]
stage=root/'.outfit-build'
if stage.exists(): shutil.rmtree(stage)
stage.mkdir()
for name in ('src','res','tools','assets'):
    if (root/name).exists(): shutil.copytree(root/name,stage/name)
shutil.copy2(root/'build.sh',stage/'build.sh')
manifest=(root/'AndroidManifest.xml').read_text()
manifest=manifest.replace('com.homestead.launcher','com.cleantv.outfit')
manifest=manifest.replace('android:label="Homestead"','android:label="Homestead Outfit"')
manifest=re.sub(r'android:versionName="([^"]+)"',r'android:versionName="\1-outfit.3"',manifest)
(stage/'AndroidManifest.xml').write_text(manifest)
for p in (stage/'src').rglob('*.java'):
    s=p.read_text().replace('package com.homestead.launcher;', 'package com.cleantv.outfit;')
    s=s.replace('"com.homestead.launcher.background"','"com.cleantv.outfit.background"')
    s=s.replace('"com.homestead.launcher.updates"','"com.cleantv.outfit.updates"')
    if p.name=='UpdateChecker.java':
        assert '"Sarg338/Homestead"' in s, 'Review upstream update identity before building'
        s=s.replace('"Sarg338/Homestead"','"hieusydo/Homestead"')
    p.write_text(s)
p=stage/'res/values/styles.xml'
s=p.read_text().replace('#FF86A7DA','#FF09131F')
s=s.replace('<item name="android:windowAnimationStyle">', '<item name="android:fontFamily">@font/outfit</item>\n    <item name="android:windowAnimationStyle">')
p.write_text(s)
print(stage)
